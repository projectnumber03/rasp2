package ru.plorum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.exception.DeviceNotFoundException;
import ru.plorum.model.Device;
import spark.utils.CollectionUtils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeviceService {

    private static final Logger log = LogManager.getLogger(DeviceService.class);

    private List<Device> devices;

    private GpioPinDigitalOutput led;

    private PropertiesService propertiesService;

    private volatile boolean isLightOn = false;

    public DeviceService(final PropertiesService propertiesService) {
        try {
            this.propertiesService = propertiesService;
            final List<String> buttonPins = propertiesService.getStringList("button.pins");
            final String ledPin = propertiesService.getString("led.pin");
            final List<String> devicesId = propertiesService.getStringList("devices.id");
            final String serverAddress = propertiesService.getString("server.address");
            this.led = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.getPinByName(ledPin));
            this.led.setShutdownOptions(true);
            this.devices = IntStream.range(0, buttonPins.size()).boxed().map(i -> {
                final UUID deviceId = Optional.ofNullable(getDeviceIdFromServer(i)).orElse(propertiesService.getDeviceId(i));
                return new Device(deviceId, buttonPins.get(i), serverAddress);
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(devicesId)) {
                propertiesService.saveDevicesId(this.devices.stream().map(Device::getId).map(UUID::toString).collect(Collectors.toList()));
            }
            monitorAlerts();
        } catch (Exception e) {
            log.error("unable to initiate devices", e);
        }
    }

    private void monitorAlerts() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (isLightOn) {
                return;
            }
            if (devices.stream().map(Device::getStatus).anyMatch(Device.Status.ALERT::equals)) {
                this.led.high();
                return;
            }
            if (devices.stream().map(Device::getStatus).allMatch(Device.Status.STANDBY::equals)) {
                this.led.low();
            }
        }, 0, 1, TimeUnit.SECONDS);
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
            return getStatus(id);
        } catch (DeviceNotFoundException e) {
            log.error("unable to set device status", e);
            return "";
        }
    }

    public String lightOn() {
        this.led.high();
        isLightOn = true;
        return getAll();
    }

    public String lightOff() {
        this.led.low();
        isLightOn = false;
        return getAll();
    }

    public String setId(final UUID newId, final Integer pin) {
        try {
            final Device device = devices.get(pin - 1);
            device.setId(newId);
            propertiesService.updateDevicesId(this.devices.stream().map(Device::getId).map(UUID::toString).collect(Collectors.toList()));
            return getStatus(newId);
        } catch (Exception e) {
            log.error("unable to set id", e);
            return "";
        }
    }

    public String push(final UUID id) {
        try {
            final Device device = getDeviceById(id);
            device.push();
            return getStatus(id);
        } catch (DeviceNotFoundException e) {
            log.error("unable to push device button", e);
            return "";
        }
    }

    public UUID getDeviceIdFromServer(final Integer pin) {
        try {
            final String serverAddress = propertiesService.getString("server.address");
            final String serverPort = propertiesService.getString("server.port");
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(String.format("http://%s:%s/get-device-id?ip=%s&pin=%d", serverAddress, serverPort, getIp(), pin + 1))).GET().build();
            return UUID.fromString(HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body());
        } catch (Exception e) {
            log.error("unable get device id from server", e);
            return null;
        }
    }

    private String getIp() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            log.error("unable get ip address", e);
            return "";
        }
    }

}
