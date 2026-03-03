package com.smartkit.toolbox.model.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交付件信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artifact {

    /**
     * 交付件名称
     */
    private String name;

    /**
     * 交付件路径（相对于工作目录）
     */
    private String path;

    /**
     * 交付件大小（字节）
     */
    private Long size;
}