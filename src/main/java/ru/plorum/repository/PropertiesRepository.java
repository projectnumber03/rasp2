package ru.plorum.repository;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.List;

public class PropertiesRepository {

    private PropertiesConfiguration config;

    private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

    public void init() throws Exception {
        this.builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().fileBased().setFile(new File("application.properties")).setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        this.config = this.builder.getConfiguration();
    }

    public String getString(final String key) {
        return config.getString(key);
    }

    public List<String> getStringList(final String key) {
        return config.getList(String.class, key);
    }

    public Integer getInt(final String key) {
        return config.getInt(key);
    }

    public void saveDevicesId(final List<String> values) throws ConfigurationException {
        config.addProperty("devices.id", values);
        builder.save();
    }

    public void updateDevicesId(final List<String> values) throws ConfigurationException {
        config.setProperty("devices.id", values);
        builder.save();
    }

}
