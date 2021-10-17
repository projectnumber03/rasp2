package ru.plorum.repository;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import java.io.File;

public class PropertiesRepository {

    private PropertiesConfiguration config;

    public void init() throws Exception {
        this.config = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().fileBased().setFile(new File("application.properties")))
                .getConfiguration();
    }

    public String[] getArray(final String key) {
        return config.getStringArray(key);
    }

    public Integer getInt(final String key) {
        return config.getInt(key);
    }

}
