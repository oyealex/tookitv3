package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;

import java.util.List;

public interface DeviceService {

    Device addDevice(DeviceCreateDTO dto);

    BatchResultDTO addDevices(List<DeviceCreateDTO> devices);

    Device getDevice(String ip);

    List<Device> getDevices(DeviceQueryDTO query);

    long count();

    long count(DeviceQueryDTO query);

    Device updateDevice(String ip, DeviceUpdateDTO dto);

    void deleteDevice(String ip);

    BatchResultDTO deleteDevices(List<String> ips);
}