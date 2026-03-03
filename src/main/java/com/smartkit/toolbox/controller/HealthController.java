package com.smartkit.toolbox.controller;

import com.smartkit.toolbox.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Health", description = "健康检查 API")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        );
        return Result.success(data);
    }

}