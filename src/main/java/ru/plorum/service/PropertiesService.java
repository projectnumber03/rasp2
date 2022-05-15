package ru.plorum.service;

import org.apache.logging.log4j.LogManager;
import ru.plorum.repository.PropertiesRepository;

import java.util.List;
import java.util.UUID;

public enum PropertiesService {

    INSTANCE;

    private final PropertiesRepository propertiesRepository = PropertiesRepository.INSTANCE;

    public Integer getInt(final String key) {
        return propertiesRepository.getInt(key);
    }

    public List<String> getStringList(final String key) {
        return propertiesRepository.getStringList(key);
    }

    public String getString(final String key) {
        return propertiesRepository.getString(key);
    }

    public Boolean getBoolean(final String key) {
        return propertiesRepository.getBoolean(key);
    }

    public void updateDevicesId(final List<String> values) {
        try {
            propertiesRepository.updateDevicesId(values);
        } catch (Exception e) {
            LogManager.getLogger(this.getClass()).error("unable to update devices id", e);
        }
    }

    public UUID getDeviceId(int index) {
        try {
            return UUID.fromString(getStringList("devices.id").get(index));
        } catch (Exception e) {
            return UUID.randomUUID();
        }
    }

}
