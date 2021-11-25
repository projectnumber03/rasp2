package ru.plorum.model.task;

import ru.plorum.model.Device;
import ru.plorum.service.DeviceService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface Task {
    
    default void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(getBody(), 0, getInterval(), TimeUnit.SECONDS);
    }

    default List<Device> getDevices() {
        return DeviceService.INSTANCE.getDevices();
    }

    long getInterval();

    Runnable getBody();

}
