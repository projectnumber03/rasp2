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

    @JsonProperty
    String ledPin;

    GpioController gpio;

    GpioPinDigitalInput button;

    GpioPinDigitalOutput led;

    public Device(final String buttonPin, final String ledPin) {
        this.id = UUID.randomUUID();
        this.buttonPin = buttonPin;
        this.ledPin = ledPin;
        this.status = Status.STANDBY;
        this.gpio = GpioFactory.getInstance();
        this.button = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(buttonPin), PinPullResistance.PULL_DOWN);
        this.led = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(ledPin));
        this.led.setShutdownOptions(true);
        this.button.setShutdownOptions(true);
        this.button.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isLow()) {
                setStatus(Status.ALERT);
                lightOn();
            }
        });
    }

    public void lightOn() {
        this.led.high();
    }

    public void lightOff() {
        this.led.low();
    }

    public enum Status {
        STANDBY,
        ALERT
    }

}
