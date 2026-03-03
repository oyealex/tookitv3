package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.repository.OperationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作日志异步写入器
 * 独立线程持续将缓存中的日志写入数据库
 */
@Component
public class OperationLogWriter {

    private static final Logger log = LoggerFactory.getLogger(OperationLogWriter.class);
    private static final long WRITE_INTERVAL_MS = 1000; // 每秒检查一次
    private static final int BATCH_SIZE = 50;

    private final OperationLogCache cache;
    private final OperationLogRepository repository;
    private volatile boolean running = true;
    private Thread writerThread;

    public OperationLogWriter(OperationLogCache cache, OperationLogRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWriter() {
        // 启动时从磁盘恢复缓存
        cache.recoverFromFile();

        writerThread = new Thread(this::runWriter, "OperationLogWriter");
        writerThread.setDaemon(true);
        writerThread.start();
        log.info("Operation log writer started");
    }

    private void runWriter() {
        while (running) {
            try {
                if (!cache.isEmpty()) {
                    List<OperationLog> logs = cache.drainAll();
                    if (!logs.isEmpty()) {
                        // 分批写入
                        for (int i = 0; i < logs.size(); i += BATCH_SIZE) {
                            int end = Math.min(i + BATCH_SIZE, logs.size());
                            List<OperationLog> batch = logs.subList(i, end);
                            try {
                                int[] results = repository.batchInsert(batch);
                                int successCount = 0;
                                for (int result : results) {
                                    if (result > 0) {
                                        successCount++;
                                    }
                                }
                                log.info("Batch wrote {} operation logs to database", successCount);
                            } catch (Exception e) {
                                // 写入失败，将日志放回缓存
                                cache.addAll(batch);
                                log.error("Failed to write operation logs to database, will retry", e);
                            }
                        }
                        // 写入成功后清理磁盘缓存文件
                        cache.clearCacheFile();
                    }
                }
            } catch (Exception e) {
                log.error("Error in operation log writer", e);
            }

            try {
                Thread.sleep(WRITE_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 停止写入器（应用关闭时调用）
     */
    public void stop() {
        running = false;
        if (writerThread != null) {
            writerThread.interrupt();
            try {
                writerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Operation log writer stopped");
    }
}