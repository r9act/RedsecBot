package com.mishkin.redsecbot.infrastructure.cassandra.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * @author a.mishkin
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.mishkin.redsecbot.db.repo")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @NotNull
    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }
}

