package com.smartkit.toolbox.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartkit.toolbox.model.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 操作日志内存缓存
 * 支持添加和批量获取操作日志，并持久化到磁盘
 */
@Component
public class OperationLogCache {

    private static final Logger log = LoggerFactory.getLogger(OperationLogCache.class);
    private static final String CACHE_FILE = "data/operation_log_cache.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final ConcurrentLinkedQueue<OperationLog> cache = new ConcurrentLinkedQueue<>();

    public OperationLogCache() {
        // 确保目录存在
        File cacheFile = new File(CACHE_FILE);
        File parentDir = cacheFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    /**
     * 添加操作日志到缓存
     */
    public void add(OperationLog log) {
        cache.offer(log);
        persistToFile();
    }

    /**
     * 批量添加操作日志到缓存
     */
    public void addAll(List<OperationLog> logs) {
        cache.addAll(logs);
        persistToFile();
    }

    /**
     * 获取并移除所有缓存的日志
     */
    public List<OperationLog> drainAll() {
        List<OperationLog> logs = cache.stream().toList();
        cache.clear();
        return logs;
    }

    /**
     * 获取缓存数量
     */
    public int size() {
        return cache.size();
    }

    /**
     * 判断缓存是否为空
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * 持久化到磁盘文件
     */
    private void persistToFile() {
        try {
            List<OperationLog> logs = cache.stream().toList();
            objectMapper.writeValue(new File(CACHE_FILE), logs);
        } catch (IOException e) {
            log.error("Failed to persist operation log cache to file", e);
        }
    }

    /**
     * 从磁盘文件恢复缓存
     */
    public void recoverFromFile() {
        File cacheFile = new File(CACHE_FILE);
        if (!cacheFile.exists()) {
            return;
        }

        try {
            List<OperationLog> logs = objectMapper.readValue(cacheFile,
                new TypeReference<List<OperationLog>>() {});
            if (logs != null && !logs.isEmpty()) {
                cache.addAll(logs);
                log.info("Recovered {} operation logs from cache file", logs.size());
            }
        } catch (IOException e) {
            log.error("Failed to recover operation log cache from file", e);
        }
    }

    /**
     * 清理磁盘缓存文件（写入数据库成功后调用）
     */
    public void clearCacheFile() {
        File cacheFile = new File(CACHE_FILE);
        if (cacheFile.exists()) {
            boolean deleted = cacheFile.delete();
            if (deleted) {
                log.info("Cleared operation log cache file after successful database write");
            }
        }
    }
}