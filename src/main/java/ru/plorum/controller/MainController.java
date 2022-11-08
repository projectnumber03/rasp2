package ru.plorum.controller;

import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import ru.plorum.model.command.Command;
import ru.plorum.model.task.Task;
import ru.plorum.service.DeviceService;
import ru.plorum.service.PropertiesService;

import java.util.Set;

import static spark.Spark.port;

@Log4j2
public class MainController {
    
    private static final String COMMAND_PACKAGE = "ru.plorum.model.command";

    private static final String TASK_PACKAGE = "ru.plorum.model.task";

    public MainController() {
        port(PropertiesService.INSTANCE.getInt("application.port"));
        initCommands();
        runTasks();
        log.info("{}: {} devices loaded", PropertiesService.INSTANCE.getDeviceName(), DeviceService.INSTANCE.countDevices());
        log.info("{}: == REST has started ==", PropertiesService.INSTANCE.getDeviceName());
    }

    public void initCommands() {
        final Reflections reflections = new Reflections(COMMAND_PACKAGE);
        final Set<Class<? extends Command>> commands = reflections.getSubTypesOf(Command.class);
        for (final Class<? extends Command> c : commands) {
            try {
                c.getDeclaredConstructor().newInstance().execute();
            } catch (Exception e) {
                log.error("{}: failed to init command {}", PropertiesService.INSTANCE.getDeviceName(), c.getName(), e);
            }
        }
        log.info("{}: {} commands loaded", PropertiesService.INSTANCE.getDeviceName(), commands.size());
    }

    public void runTasks() {
        final Reflections reflections = new Reflections(TASK_PACKAGE);
        final Set<Class<? extends Task>> tasks = reflections.getSubTypesOf(Task.class);
        for (final Class<? extends Task> t : tasks) {
            try {
                t.getDeclaredConstructor().newInstance().run();
            } catch (Exception e) {
                log.error("{}: failed to run task {}", PropertiesService.INSTANCE.getDeviceName(), t.getName(), e);
            }
        }
        log.info("{}: {} tasks runned", PropertiesService.INSTANCE.getDeviceName(), tasks.size());
    }

}
