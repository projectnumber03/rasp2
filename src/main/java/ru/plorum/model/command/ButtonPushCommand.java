package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

import static java.util.UUID.fromString;

public class ButtonPushCommand implements Command {

    @Override
    public String getUrl() {
        return "/button/:id";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            final String deviceId = request.params(":id");
            LogManager.getLogger(this.getClass()).info("Push device {} button", deviceId);
            try {
                return DeviceService.INSTANCE.push(fromString(deviceId));
            } catch (Exception e) {
                return DeviceService.INSTANCE.push(Integer.parseInt(deviceId));
            }
        };
    }

}
