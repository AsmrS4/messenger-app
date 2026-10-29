package com.example.authorization_service.configuration;

import com.example.authorization_service.core.persistence.AccessTokenRepository;
import com.example.authorization_service.core.persistence.UserCredentialsRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class JdbiConfiguration {
    @Bean
    public SqlObjectPlugin sqlObjectPlugin() {
        return new SqlObjectPlugin();
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(proxy);

        jdbiPlugins.forEach(jdbi::installPlugin);
        rowMappers.forEach(jdbi::registerRowMapper);

        return jdbi;
    }

    @Bean
    public UserCredentialsRepository userCredentialsRepository(Jdbi jdbi) {
        return jdbi.onDemand(UserCredentialsRepository.class);
    }

    @Bean
    public AccessTokenRepository accessTokenRepository( Jdbi jdbi ) {
        return jdbi.onDemand(AccessTokenRepository.class);
    }
}