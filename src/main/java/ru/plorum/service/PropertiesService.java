package ru.plorum.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.plorum.repository.PropertiesRepository;

public class PropertiesService {

    private static final Logger log = LogManager.getLogger(PropertiesService.class);

    private PropertiesRepository propertiesRepository;

    public PropertiesService() {
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

    public String[] getArray(final String key) {
        return propertiesRepository.getArray(key);
    }

}
