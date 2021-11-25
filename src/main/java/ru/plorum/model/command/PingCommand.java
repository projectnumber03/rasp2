package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

public class PingCommand implements Command {

    @Override
    public String getUrl() {
        return "/ping";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            LogManager.getLogger(this.getClass()).info("ping");
            return DeviceService.INSTANCE.ping();
        };
    }

}
