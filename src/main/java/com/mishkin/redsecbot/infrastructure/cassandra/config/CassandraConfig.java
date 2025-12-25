package com.mishkin.redsecbot.infrastructure.cassandra.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.net.InetSocketAddress;

/**
 * @author a.mishkin
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.mishkin.redsecbot.db.repo")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.port}")
    private int port;

    @Value("${spring.data.cassandra.local-datacenter}")
    private String datacenter;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @NotNull
    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected String getLocalDataCenter() {
        return datacenter;
    }

    /**
     * Вместо cassandraSession() мы переопределяем cassandraSessionFactory.
     * Именно здесь мы вешаем @DependsOn, чтобы фабрика не начала создавать
     * основную сессию до того, как отработает инициализатор (!грязное решение)
     */
    @NotNull
    @Bean
    @Primary
    @Override
    @DependsOn("cassandraSchemaInitializer")
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean factory = super.cassandraSession();
        factory.setContactPoints(contactPoints);
        factory.setPort(port);
        factory.setLocalDatacenter(datacenter);
        factory.setKeyspaceName(getKeyspaceName());
        return factory;
    }

    @Bean
    @Profile("dev")
    public CqlSession adminCqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(datacenter)
                .build();
    }
}

