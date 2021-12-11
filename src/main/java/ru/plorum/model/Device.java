package ru.plorum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.jet.datamodel.Tuple3;
import com.hazelcast.jet.datamodel.Tuple4;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.plorum.service.PropertiesService;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;

import static com.pi4j.wiringpi.Gpio.millis;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Device {

    @Getter
    @Setter
    @JsonProperty
    UUID id;

    @Getter
    @Setter
    @JsonProperty
    Status status;

    @Setter
    @JsonProperty
    String buttonPin;

    GpioController gpio;

    GpioPinDigitalInput button;

    HazelcastInstance hazelcastInstanceClient;

    Long buttonTimer = 0L;

    public Device(final UUID id, final String buttonPin) {
        this.buttonPin = buttonPin;
        this.status = Status.STANDBY;
        this.hazelcastInstanceClient = HazelcastClient.newHazelcastClient(getHazelCastConfig(PropertiesService.INSTANCE.getString("server.address")));
        this.id = getId(id);
        if (PropertiesService.INSTANCE.getBoolean("fake.devices")) return;
        this.gpio = GpioFactory.getInstance();
        this.button = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(buttonPin), PinPullResistance.PULL_DOWN);
        this.button.setShutdownOptions(true);
        this.button.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isLow() && millis() - buttonTimer > 300) {
                push();
                buttonTimer = millis();
            }
        });
    }

    public void refreshId() {
        setId(getId(this.id));
    }

    private UUID getId(final UUID id) {
        final Map<String, Map<Integer, UUID>> deviceGroups = this.hazelcastInstanceClient.getMap("devices");
        final String formattedDeviceIp = String.format("%s:%s", getIp(), PropertiesService.INSTANCE.getString("application.port"));
        final Map<Integer, UUID> deviceGroup = deviceGroups.getOrDefault(formattedDeviceIp, Collections.emptyMap());
        final UUID deviceId = deviceGroup.getOrDefault(getPin(), null);
        return Optional.ofNullable(deviceId).orElse(id);
    }

    private String getIp() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    private int getPin() {
        return PropertiesService.INSTANCE.getStringList("button.pins").indexOf(buttonPin) + 1;
    }

    public void sendAlertEvent() {
        if (!isHazelcastRunning()) return;
        final Map<UUID, LocalDateTime> alerts = hazelcastInstanceClient.getMap("alerts");
        alerts.put(this.id, LocalDateTime.now());
    }

    public void sendNewDeviceEvent() {
        if (!isHazelcastRunning()) return;
        final List<Tuple4<UUID, String, Integer, Integer>> newDevices = this.hazelcastInstanceClient.getList("newDevices");
        final String formattedDeviceIp = String.format("%s:%s", getIp(), PropertiesService.INSTANCE.getString("application.port"));
        final Integer healthCheckTimeout = PropertiesService.INSTANCE.getInt("healthcheck.timeout");
        newDevices.add(Tuple4.tuple4(id, formattedDeviceIp, getPin(), healthCheckTimeout));
    }

    public void sendHealthCheckEvent() {
        if (!isHazelcastRunning()) return;
        final List<Tuple3<UUID, UUID, LocalDateTime>> healthChecks = hazelcastInstanceClient.getList("healthChecks");
        healthChecks.add(Tuple3.tuple3(UUID.randomUUID(), this.id, LocalDateTime.now()));
    }

    private ClientConfig getHazelCastConfig(final String serverAddress) {
        final ClientConfig config = new ClientConfig();
        config.setClusterName("dev");
        config.getNetworkConfig().addAddress(serverAddress);
        config.getConnectionStrategyConfig()
                .getConnectionRetryConfig()
                .setInitialBackoffMillis(5000)
                .setClusterConnectTimeoutMillis(Long.MAX_VALUE);
        return config;
    }

    public void push() {
        sendAlertEvent();
        setStatus(Status.ALERT);
    }

    private Boolean isHazelcastRunning() {
        return Optional.ofNullable(hazelcastInstanceClient)
                .map(HazelcastInstance::getLifecycleService)
                .map(LifecycleService::isRunning)
                .orElse(false);
    }

    public enum Status {
        STANDBY,
        ALERT
    }

}
