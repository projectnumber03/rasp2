package ru.plorum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.exception.DeviceNotFoundException;
import ru.plorum.model.Device;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeviceService {

    private static final Logger log = LogManager.getLogger(DeviceService.class);

    private List<Device> devices;

    public DeviceService(final PropertiesService propertiesService) {
        try {
            final String[] buttonPins = propertiesService.getArray("button.pins");
            final String[] ledPins = propertiesService.getArray("led.pins");
            final String serverAddress = propertiesService.getString("server.address");
            this.devices = IntStream.range(0, Math.min(buttonPins.length, ledPins.length)).boxed().map(i -> new Device(buttonPins[i], ledPins[i], serverAddress)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("unable to initiate devices", e);
        }
    }

    private Device getDeviceById(final UUID id) throws DeviceNotFoundException {
        return devices.stream().filter(d -> d.getId().equals(id)).findAny().orElseThrow(DeviceNotFoundException::new);
    }

    public String getAll() {
        try {
            return new ObjectMapper().writeValueAsString(devices);
        } catch (JsonProcessingException e) {
            log.error("unable to marshal devices", e);
            return "";
        }
    }

    public String getStatus(final UUID id) {
        try {
            return new ObjectMapper().writeValueAsString(getDeviceById(id));
        } catch (DeviceNotFoundException e) {
            log.error("unable to get device status", e);
            return "";
        } catch (JsonProcessingException e) {
            log.error("unable to marshal device status", e);
            return "";
        }
    }

    public String resetStatus(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.setStatus(Device.Status.STANDBY);
            device.lightOff();
            return getStatus(id);
        } catch (DeviceNotFoundException e) {
            log.error("unable to set device status", e);
            return "";
        }
    }

    public String lightOn(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.lightOn();
            return getStatus(id);
        } catch (DeviceNotFoundException e) {
            log.error("unable to light on", e);
            return "";
        }
    }

    public String lightOff(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.lightOff();
            return getStatus(id);
        } catch (DeviceNotFoundException e) {
            log.error("unable to light off", e);
            return "";
        }
    }

    public String setId(final UUID newId, final Integer pin) {
        try {
            final Device device = devices.get(pin - 1);
            device.setId(newId);
            return getStatus(newId);
        } catch (Exception e) {
            log.error("unable to set id", e);
            return "";
        }
    }

}
