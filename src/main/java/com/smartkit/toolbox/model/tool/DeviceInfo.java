package com.smartkit.toolbox.model.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息 DTO，传递给子工具的设备数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    /**
     * 设备 IP 地址
     */
    private String ip;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（当前为明文，未来支持密文）
     */
    private String password;
}