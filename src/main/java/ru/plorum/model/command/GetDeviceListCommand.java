package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

public class GetDeviceListCommand implements Command {

    @Override
    public String getUrl() {
        return "/devices";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            LogManager.getLogger(this.getClass()).info("Getting device list");
            return DeviceService.INSTANCE.getAll();
        };
    }

}
