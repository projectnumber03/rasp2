package ru.plorum.model;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import ru.plorum.service.PropertiesService;

public enum Led {
    INSTANCE;

    private GpioPinDigitalOutput led;

    Led() {
        if (PropertiesService.INSTANCE.getBoolean("fake.devices")) return;
        final String ledPin = PropertiesService.INSTANCE.getString("led.pin");
        this.led = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.getPinByName(ledPin));
        this.led.setShutdownOptions(true);
    }

    public void high() {
        this.led.high();
    }

    public void low() {
        this.led.low();
    }

    public synchronized boolean isLightOn() {
        return led.isHigh();
    }

}
