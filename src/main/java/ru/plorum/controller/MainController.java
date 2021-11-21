package ru.plorum.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.service.DeviceService;
import ru.plorum.service.PropertiesService;

import static spark.Spark.*;
import static java.util.UUID.*;

public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    private final PropertiesService propertiesService = new PropertiesService();

    private final DeviceService deviceService = new DeviceService(propertiesService);

    public MainController() {
        port(propertiesService.getInt("application.port"));
        getDeviceList();
        getStatus();
        lightOn();
        lightOff();
        setStatus();
        setId();
        push();
        ping();
        log.info("== REST has started ==");
        deviceService.initHazelcastClients();
    }

    public void getDeviceList() {
        get("/getDeviceList", (request, response) -> {
            log.info("Getting device list");
            return deviceService.getAll();
        });
    }

    public void getStatus() {
        get("/getStatus/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Getting device {} status", deviceId);
            return deviceService.getStatus(fromString(deviceId));
        });
    }

    public void lightOn() {
        get("/lightOn", (request, response) -> {
            log.info("Turning on the light");
            return deviceService.lightOn();
        });
    }

    public void lightOff() {
        get("/lightOff", (request, response) -> {
            log.info("Turning off the light");
            return deviceService.lightOff();
        });
    }

    public void setStatus() {
        get("/setStatus/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Reset device {} status", deviceId);
            return deviceService.resetStatus(fromString(deviceId));
        });
    }

    public void setId() {
        get("/setId/:id/:pin", (request, response) -> {
            final String newId = request.params(":id");
            final String pin = request.params(":pin");
            log.info("Setting new id {}", newId);
            return deviceService.setId(fromString(newId), Integer.parseInt(pin));
        });
    }

    public void push() {
        get("/push/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Push device {} button", deviceId);
            return deviceService.push(fromString(deviceId));
        });
    }

    public void ping() {
        get("/ping", (request, response) -> {
            log.info("ping");
            return deviceService.ping();
        });
    }

}
