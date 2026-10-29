package com.example.authorization_service.core.persistence;

import com.example.authorization_service.core.persistence.entity.UserLoginCredentials;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.UUID;

public interface UserCredentialsRepository {
    @SqlUpdate("""
            INSERT INTO credentials(user_id, login, hash_password)
            VALUES(:userId, :login, :hashPassword)
            """)
    @GetGeneratedKeys("id")
    long saveCredentials(@BindMethods UserLoginCredentials credentials);

    @SqlQuery("""
            SELECT EXISTS(
                SELECT 1 FROM credentials
                WHERE credentials.user_id = :userId
            )
            """)
    boolean hasCredentials(@Bind("userId") UUID userId);

    @SqlUpdate("""
            UPDATE credentials SET login = :login
            WHERE credentials.user_id = :userId
            """)
    int updateUserCredentials(@Bind("login") String login, @Bind("userId")UUID userId);

    @SqlQuery("""
            SELECT user_id, login, hash_password FROM credentials
            WHERE credentials.login = :login
            """)
    @RegisterConstructorMapper(UserLoginCredentials.class)
    UserLoginCredentials findByLogin(@Bind("login")String login);
}
