package ru.plorum.model.command;

import org.apache.logging.log4j.LogManager;
import ru.plorum.service.DeviceService;
import spark.Route;

import static java.util.UUID.fromString;

public class SetIdCommand implements Command {

    @Override
    public String getUrl() {
        return "/setId/:id/:pin";
    }

    @Override
    public Route getRoute() {
        return (request, response) -> {
            final String newId = request.params(":id");
            final String pin = request.params(":pin");
            LogManager.getLogger(this.getClass()).info("Setting new id {}", newId);
            return DeviceService.INSTANCE.setId(fromString(newId), Integer.parseInt(pin));
        };
    }

}
