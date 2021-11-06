package ru.plorum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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

    @JsonProperty
    String buttonPin;

    GpioController gpio;

    GpioPinDigitalInput button;

    HazelcastInstance hazelcastInstanceClient;

    Long buttonTimer = 0L;

    public Device(final UUID id, final String buttonPin, final String serverAddress) {
        this.id = id;
        this.buttonPin = buttonPin;
        this.status = Status.STANDBY;
        this.gpio = GpioFactory.getInstance();
        this.button = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(buttonPin), PinPullResistance.PULL_DOWN);
        this.button.setShutdownOptions(true);
        this.button.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isLow() && millis() - buttonTimer > 300) {
                push();
                buttonTimer = millis();
            }
        });
        this.hazelcastInstanceClient = HazelcastClient.newHazelcastClient(getHazelCastConfig(serverAddress));
    }

    public void sendAlertEvent() {
        final Map<UUID, LocalDateTime> map = hazelcastInstanceClient.getMap("alerts");
        map.put(this.id, LocalDateTime.now());
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

    public enum Status {
        STANDBY,
        ALERT
    }

}
