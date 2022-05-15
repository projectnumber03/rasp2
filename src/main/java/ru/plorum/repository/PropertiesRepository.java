package ru.plorum.repository;

import lombok.SneakyThrows;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.List;

public enum  PropertiesRepository {

    INSTANCE;

    private final FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

    private final PropertiesConfiguration config;

    @SneakyThrows
    PropertiesRepository() {
        this.builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().fileBased().setFile(new File("application.properties")).setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        this.config = this.builder.getConfiguration();
    }

    public List<String> getStringList(final String key) {
        return config.getList(String.class, key);
    }

    public Integer getInt(final String key) {
        return config.getInt(key);
    }

    public String getString(final String key) {
        return config.getString(key);
    }

    public Boolean getBoolean(final String key) {
        return config.getBoolean(key);
    }

    public void updateDevicesId(final List<String> values) throws ConfigurationException {
        config.setProperty("devices.id", values);
        builder.save();
    }

}
