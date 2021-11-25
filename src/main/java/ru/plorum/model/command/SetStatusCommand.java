package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

import static java.util.UUID.fromString;

public class SetStatusCommand implements Command {

    @Override
    public String getUrl() {
        return "/setStatus/:id";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            final String deviceId = request.params(":id");
            LogManager.getLogger(this.getClass()).info("Reset device {} status", deviceId);
            try {
                return DeviceService.INSTANCE.resetStatus(fromString(deviceId));
            } catch (Exception e) {
                return DeviceService.INSTANCE.resetStatus(Integer.parseInt(deviceId));
            }
        };
    }

}
