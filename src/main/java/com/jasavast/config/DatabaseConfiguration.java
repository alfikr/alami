package com.jasavast.config;

import com.jasavast.core.util.JsonToMapConverter;
import com.jasavast.core.util.LocalDateToIsoDate;
import com.jasavast.core.util.MapToJsonConverter;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseConfiguration {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.r2dbc.host}")
    private String host;

    @Value("${spring.r2dbc.port}")
    private int port;
    @Value("${spring.r2dbc.username}")
    private String dbUsername;
    @Value("${spring.r2dbc.password}")
    private String dbPassword;
    @Value("${spring.r2dbc.name}")
    private String dbName;
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(PostgresqlConnectionFactory factory){
        return new R2dbcEntityTemplate(factory);
    }

//    Hanya memiliki 1 database karena dengan asumsi app dipake untuk scope kecil dan transaksi rendah
    @Bean
    public DatabaseClient databaseClient(PostgresqlConnectionFactory factory){
        return DatabaseClient.builder().connectionFactory(factory)
                .namedParameters(true)
                .build();
    }
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory){
        R2dbcDialect dialect=DialectResolver.getDialect(connectionFactory);
        List<Converter<?,?>> converters=new ArrayList<>();
        converters.add(new JsonToMapConverter());
        converters.add(new MapToJsonConverter());
        converters.add(new LocalDateToIsoDate());
        return R2dbcCustomConversions.of(dialect,converters);
    }
    @Bean
    public PostgresqlConnectionFactory factory(){
        PostgresqlConnectionFactory factory=new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .applicationName(appName)
                .database(dbName)
                .host(host)
                .port(port)
                .schema("public")
                .username(dbUsername)
                .password(dbPassword)
                .build());
        return factory;
    }
}
