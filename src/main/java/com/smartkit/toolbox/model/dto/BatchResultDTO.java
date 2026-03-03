package com.smartkit.toolbox.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResultDTO {
    private int successCount;
    private int failCount;
    private List<FailReason> failReasons;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailReason {
        private Integer row;
        private String ip;
        private String reason;
    }
}