package ru.plorum.model;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Getter;
import ru.plorum.service.PropertiesService;

public enum Led {
    INSTANCE;

    private GpioPinDigitalOutput led;

    @Getter
    private volatile boolean locked = false;

    Led() {
        if (PropertiesService.INSTANCE.getBoolean("fake.devices")) return;
        final String ledPin = PropertiesService.INSTANCE.getString("led.pin");
        this.led = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.getPinByName(ledPin));
        this.led.setShutdownOptions(true);
    }

    public void high(final boolean locked) {
        this.locked = locked;
        this.led.high();
    }

    public void high() {
        high(false);
    }

    public void low() {
        this.locked = false;
        this.led.low();
    }

}
