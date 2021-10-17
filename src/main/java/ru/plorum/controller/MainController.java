package ru.plorum.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.service.DeviceService;
import ru.plorum.service.PropertiesService;
import spark.Spark;

import java.util.UUID;


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    private final PropertiesService propertiesService = new PropertiesService();

    private final DeviceService deviceService = new DeviceService(propertiesService);

    public MainController() {
        Spark.port(propertiesService.getInt("application.port"));
        getDeviceList();
        getStatus();
        lightOn();
        lightOff();
        setStatus();
        log.info("== REST has started ==");
    }

    public void getDeviceList() {
        Spark.get("/getDeviceList", (request, response) -> {
            log.info("Getting device list");
            return deviceService.getAll();
        });
    }

    public void getStatus() {
        Spark.get("/getStatus/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Getting device {} status", deviceId);
            return deviceService.getStatus(UUID.fromString(deviceId));
        });
    }

    public void lightOn() {
        Spark.get("/lightOn/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Turning on the light, deviceId = {}", deviceId);
            return deviceService.lightOn(UUID.fromString(deviceId));
        });
    }

    public void lightOff() {
        Spark.get("/lightOff/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Turning off the light, deviceId = {}", deviceId);
            return deviceService.lightOff(UUID.fromString(deviceId));
        });
    }

    public void setStatus() {
        Spark.get("/setStatus/:id", (request, response) -> {
            final String deviceId = request.params(":id");
            log.info("Reset device {} status", deviceId);
            return deviceService.resetStatus(UUID.fromString(deviceId));
        });
    }

}