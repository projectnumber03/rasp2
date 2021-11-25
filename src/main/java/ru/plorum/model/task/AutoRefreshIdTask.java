package ru.plorum.model.task;

import org.apache.logging.log4j.LogManager;
import ru.plorum.model.Device;
import ru.plorum.service.PropertiesService;

import java.util.UUID;
import java.util.stream.Collectors;

public class AutoRefreshIdTask implements Task {

    @Override
    public long getInterval() {
        return 15;
    }

    @Override
    public Runnable getBody() {
        return () -> {
            LogManager.getLogger(this.getClass()).info("refreshing id's");
            getDevices().forEach(Device::refreshId);
            PropertiesService.INSTANCE.updateDevicesId(getDevices().stream().map(Device::getId).map(UUID::toString).collect(Collectors.toList()));
        };
    }

}
