package com.community.rest.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class H2DBConfig {
    
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "8010");
    }
}