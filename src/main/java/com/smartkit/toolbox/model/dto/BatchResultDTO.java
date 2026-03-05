package com.smartkit.toolbox.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 批量操作结果DTO，用于返回批量操作的成功/失败统计信息。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResultDTO {
    /**
     * 成功数量
     */
    private int successCount;

    /**
     * 失败数量
     */
    private int failCount;

    /**
     * 失败原因列表
     */
    private List<FailReason> failReasons;

    /**
     * 失败原因内部类，记录单条失败信息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailReason {
        /**
         * 行号
         */
        private Integer row;

        /**
         * 设备IP地址
         */
        private String ip;

        /**
         * 失败原因描述
         */
        private String reason;
    }
}