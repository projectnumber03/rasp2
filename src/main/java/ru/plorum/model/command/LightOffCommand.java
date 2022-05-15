package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

public class LightOffCommand implements Command {

    @Override
    public String getUrl() {
        return "/light/off";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            LogManager.getLogger(this.getClass()).info("Turning off the light");
            return DeviceService.INSTANCE.lightOff();
        };
    }

}
