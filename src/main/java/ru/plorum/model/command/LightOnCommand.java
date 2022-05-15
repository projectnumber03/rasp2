package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

public class LightOnCommand implements Command {

    @Override
    public String getUrl() {
        return "/light/on";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            LogManager.getLogger(this.getClass()).info("Turning on the light");
            return DeviceService.INSTANCE.lightOn();
        };
    }

}
