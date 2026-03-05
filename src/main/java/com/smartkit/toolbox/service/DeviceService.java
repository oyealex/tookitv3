package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;

import java.util.List;

/**
 * 设备服务接口，定义设备管理的业务操作。
 *
 * @author SmartKit
 * @since 1.0.0
 */
public interface DeviceService {

    /**
     * 添加单个设备
     *
     * @param dto 设备创建DTO
     * @return 创建的设备对象
     */
    Device addDevice(DeviceCreateDTO dto);

    /**
     * 批量添加设备
     *
     * @param devices 设备创建DTO列表
     * @return 批量操作结果
     */
    BatchResultDTO addDevices(List<DeviceCreateDTO> devices);

    /**
     * 根据IP地址查询设备
     *
     * @param ip 设备IP地址
     * @return 设备对象
     */
    Device getDevice(String ip);

    /**
     * 查询设备列表
     *
     * @param query 查询条件DTO
     * @return 设备列表
     */
    List<Device> getDevices(DeviceQueryDTO query);

    /**
     * 统计设备总数
     *
     * @return 设备总数
     */
    long count();

    /**
     * 根据条件统计设备数量
     *
     * @param query 查询条件DTO
     * @return 设备数量
     */
    long count(DeviceQueryDTO query);

    /**
     * 更新设备信息
     *
     * @param ip 设备IP地址
     * @param dto 设备更新DTO
     * @return 更新后的设备对象
     */
    Device updateDevice(String ip, DeviceUpdateDTO dto);

    /**
     * 删除单个设备
     *
     * @param ip 设备IP地址
     */
    void deleteDevice(String ip);

    /**
     * 批量删除设备
     *
     * @param ips 设备IP地址列表
     * @return 批量操作结果
     */
    BatchResultDTO deleteDevices(List<String> ips);
}