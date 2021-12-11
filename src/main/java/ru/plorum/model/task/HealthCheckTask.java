package ru.plorum.model.task;

import org.apache.logging.log4j.LogManager;
import ru.plorum.model.Device;
import ru.plorum.service.DeviceService;
import ru.plorum.service.PropertiesService;

import java.util.List;

public class HealthCheckTask implements Task {

    private final List<Device> devices = DeviceService.INSTANCE.getDevices();

    @Override
    public long getInterval() {
        return PropertiesService.INSTANCE.getInt("healthcheck.timeout");
    }

    @Override
    public Runnable getBody() {
        return () -> devices.stream().peek(d -> LogManager.getLogger(this.getClass()).info("sending healthcheck from device " + d.getId())).forEach(Device::sendHealthCheckEvent);
    }

}
