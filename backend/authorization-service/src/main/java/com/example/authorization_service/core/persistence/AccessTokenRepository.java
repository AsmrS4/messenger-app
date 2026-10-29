package com.example.authorization_service.core.persistence;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface AccessTokenRepository {
    @SqlUpdate("""
            INSERT INTO blacklist(token)
            VALUES(:accessToken)
            """)
    void addToBlacklist(@Bind("accessToken") String accessToken);

    @SqlQuery("""
            SELECT EXISTS(
                SELECT 1 FROM blacklist WHERE blacklist.token = :accessToken
            )
            """)
    boolean isBlacklisted(@Bind("accessToken") String accessToken);
}
