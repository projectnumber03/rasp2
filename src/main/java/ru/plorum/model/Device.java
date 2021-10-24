package ru.plorum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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

    Long buttonTimer = 0L;

    public Device(final UUID id, final String buttonPin) {
        this.id = id;
        this.buttonPin = buttonPin;
        this.status = Status.STANDBY;
        this.gpio = GpioFactory.getInstance();
        this.button = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(buttonPin), PinPullResistance.PULL_DOWN);
        this.button.setShutdownOptions(true);
        this.button.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isLow() && millis() - buttonTimer > 300) {
                setStatus(Status.ALERT);
                buttonTimer = millis();
            }
        });
    }

    public enum Status {
        STANDBY,
        ALERT
    }

}
