package ru.plorum.model.command;

import spark.Route;

import static spark.Spark.get;

public interface Command {

    String getUrl();

    Route getRoute();

    default void execute() {
        get(getUrl(), getRoute());
    }

}
