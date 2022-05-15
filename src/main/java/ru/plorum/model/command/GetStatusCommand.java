package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

import static java.util.UUID.fromString;

public class GetStatusCommand implements Command {

    @Override
    public String getUrl() {
        return "/device/:id";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            final String deviceId = request.params(":id");
            LogManager.getLogger(this.getClass()).info("Getting device {} status", deviceId);
            return DeviceService.INSTANCE.getById(fromString(deviceId));
        };
    }

}
