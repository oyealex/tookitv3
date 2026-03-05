package com.smartkit.toolbox.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务类，提供操作日志的记录、查询和导出功能。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Service
public class OperationLogService {

    /**
     * 最大查询数量限制
     */
    private static final int MAX_LIMIT = 100;

    /**
     * 默认查询数量
     */
    private static final int DEFAULT_LIMIT = 20;

    /**
     * 操作日志仓库
     */
    private final OperationLogRepository repository;

    /**
     * 操作日志缓存
     */
    private final OperationLogCache cache;

    /**
     * 操作日志国际化工具
     */
    private final OperationLogI18nUtil i18nUtil;

    /**
     * 构造方法，注入依赖的服务
     *
     * @param repository 操作日志仓库
     * @param cache 操作日志缓存
     * @param i18nUtil 操作日志国际化工具
     */
    public OperationLogService(OperationLogRepository repository,
                               OperationLogCache cache,
                               OperationLogI18nUtil i18nUtil) {
        this.repository = repository;
        this.cache = cache;
        this.i18nUtil = i18nUtil;
    }

    /**
     * 同步添加操作日志，直接写入数据库
     *
     * @param log 操作日志对象
     */
    public void logOperation(OperationLog log) {
        // 处理国际化描述
        if (log.getDescription() != null) {
            String localizedDescription = i18nUtil.getLocalizedMessage(log.getDescription());
            log.setDescription(localizedDescription);
        }

        log.setOperationTime(LocalDateTime.now());
        repository.insert(log);
    }

    /**
     * 异步添加操作日志，写入缓存队列
     *
     * @param log 操作日志对象
     */
    public void logOperationAsync(OperationLog log) {
        // 处理国际化描述
        if (log.getDescription() != null) {
            String localizedDescription = i18nUtil.getLocalizedMessage(log.getDescription());
            log.setDescription(localizedDescription);
        }

        log.setOperationTime(LocalDateTime.now());
        cache.add(log);
    }

    /**
     * 批量查询操作日志
     *
     * @param offset 查询偏移量
     * @param limit 查询数量限制
     * @param keyword 关键字搜索
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param result 操作结果过滤
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 操作日志列表
     */
    public List<OperationLog> queryLogs(Integer offset, Integer limit, String keyword,
                                         LocalDateTime startTime, LocalDateTime endTime,
                                         OperationResult result, String sortBy, String sortOrder) {
        // 参数校验和默认值设置
        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }

        return repository.findAll(offset, limit, keyword, startTime, endTime, result, sortBy, sortOrder);
    }

    /**
     * 统计操作日志数量
     *
     * @param keyword 关键字搜索
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param result 操作结果过滤
     * @return 日志总数
     */
    public long countLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime,
                          OperationResult result) {
        return repository.count(keyword, startTime, endTime, result);
    }

    /**
     * 导出操作日志到 Excel 文件
     *
     * @param response HTTP响应对象
     * @param offset 查询偏移量
     * @param limit 查询数量限制
     * @param keyword 关键字搜索
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param result 操作结果过滤
     * @throws IOException IO异常
     */
    public void exportLogs(HttpServletResponse response, Integer offset, Integer limit,
                           String keyword, LocalDateTime startTime, LocalDateTime endTime,
                           OperationResult result) throws IOException {
        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (limit == null || limit <= 0) {
            limit = MAX_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }

        List<OperationLog> logs = repository.findAll(offset, limit, keyword, startTime, endTime, result, "operationtime", "desc");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=operation_logs.xlsx");

        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), OperationLog.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("操作日志").build();
        excelWriter.write(logs, writeSheet);
        excelWriter.finish();
    }

    /**
     * 获取当前语言环境
     *
     * @return 当前语言环境字符串
     */
    public String getCurrentLocale() {
        return LocaleContextHolder.getLocale().toString();
    }
}