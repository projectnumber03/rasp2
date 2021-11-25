package ru.plorum.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.repository.PropertiesRepository;

import java.util.List;
import java.util.UUID;

public enum  PropertiesService {
    INSTANCE;

    private final Logger log = LogManager.getLogger(PropertiesService.class);

    private PropertiesRepository propertiesRepository;

    PropertiesService() {
        try {
            this.propertiesRepository = new PropertiesRepository();
            this.propertiesRepository.init();
        } catch (Exception e) {
            log.error("unable to initiate properties repository", e);
        }
    }

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
            log.error("unable to update devices id", e);
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
