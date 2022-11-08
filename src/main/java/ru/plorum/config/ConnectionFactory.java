package ru.plorum.config;

import lombok.NoArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.plorum.service.PropertiesService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@NoArgsConstructor
public class ConnectionFactory {

    private static BasicDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        if (Objects.nonNull(dataSource)) return dataSource.getConnection();
        dataSource = new BasicDataSource();
        dataSource.setUrl(PropertiesService.INSTANCE.getString("datasource.url"));
        dataSource.setDriverClassName(PropertiesService.INSTANCE.getString("datasource.driverClassName"));
        dataSource.setUsername(PropertiesService.INSTANCE.getString("datasource.username"));
        dataSource.setPassword(PropertiesService.INSTANCE.getString("datasource.password"));
        return dataSource.getConnection();
    }

}
