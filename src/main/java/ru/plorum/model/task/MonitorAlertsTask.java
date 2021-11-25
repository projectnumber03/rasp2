package ru.plorum.model.task;

import ru.plorum.model.Device;
import ru.plorum.model.Led;
import ru.plorum.service.PropertiesService;

public class MonitorAlertsTask implements Task {

    @Override
    public long getInterval() {
        return 1;
    }

    @Override
    public Runnable getBody() {
        return () -> {
            if (PropertiesService.INSTANCE.getBoolean("fake.devices")) return;
            if (Led.INSTANCE.isLightOn()) {
                return;
            }
            if (getDevices().stream().map(Device::getStatus).anyMatch(Device.Status.ALERT::equals)) {
                Led.INSTANCE.high();
                return;
            }
            if (getDevices().stream().map(Device::getStatus).allMatch(Device.Status.STANDBY::equals)) {
                Led.INSTANCE.low();
            }
        };
    }

}
