package ru.plorum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.plorum.exception.DeviceNotFoundException;
import ru.plorum.model.Device;
import ru.plorum.model.Led;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public enum DeviceService {

    INSTANCE;

    final List<String> buttonPins = PropertiesService.INSTANCE.getStringList("button.pins");

    @Getter
    private final List<Device> devices = IntStream.range(0, buttonPins.size()).boxed()
            .map(i -> new Device(PropertiesService.INSTANCE.getDeviceId(i), buttonPins.get(i)))
            .peek(Device::sendNewDeviceEvent)
            .collect(Collectors.toList());

    private Device getDeviceById(final UUID id) throws DeviceNotFoundException {
        return devices.stream().filter(d -> d.getId().equals(id)).findAny().orElseThrow(DeviceNotFoundException::new);
    }

    public String getAll() {
        try {
            return new ObjectMapper().writeValueAsString(devices);
        } catch (JsonProcessingException e) {
            log.error("{}: unable to marshal devices", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String getById(final UUID id) {
        try {
            return new ObjectMapper().writeValueAsString(getDeviceById(id));
        } catch (DeviceNotFoundException e) {
            log.error("{}: unable to get device status", PropertiesService.INSTANCE.getDeviceName(),  e);
            return "";
        } catch (JsonProcessingException e) {
            log.error("{}: unable to marshal device status", PropertiesService.INSTANCE.getDeviceName(),  e);
            return "";
        }
    }

    public String resetStatus(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.setStatus(Device.Status.STANDBY);
            return getById(id);
        } catch (DeviceNotFoundException e) {
            log.error("{}: unable to set device status", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String resetStatus(int pin) {
        try {
            final Device device = devices.get(pin - 1);
            device.setStatus(Device.Status.STANDBY);
            return getById(device.getId());
        } catch (Exception e) {
            log.error("{}: unable to set device status", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String lightOn() {
        Led.INSTANCE.high(true);
        return getAll();
    }

    public String lightOff() {
        Led.INSTANCE.low();
        return getAll();
    }

    public String setId(final UUID newId, final Integer pin) {
        try {
            final Device device = devices.get(pin - 1);
            device.setId(newId);
            PropertiesService.INSTANCE.updateDevicesId(this.devices.stream().map(Device::getId).map(UUID::toString).collect(Collectors.toList()));
            return getById(newId);
        } catch (Exception e) {
            log.error("{}: unable to set id", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String push(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.push();
            return getById(id);
        } catch (DeviceNotFoundException e) {
            log.error("{}: unable to push device button", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String push(final Integer pin) {
        try {
            final Device device = devices.get(pin - 1);
            device.push();
            return getById(device.getId());
        } catch (Exception e) {
            log.error("{}: unable to push device button", PropertiesService.INSTANCE.getDeviceName(), e);
            return "";
        }
    }

    public String ping() {
        return "pong";
    }

    public int countDevices() {
        return devices.size();
    }

}
