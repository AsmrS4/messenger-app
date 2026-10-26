package com.example.user_service.core.persistence;

import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.models.UpdatePayload;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserRepo {
    @SqlUpdate(
            "INSERT INTO users(full_name, email, login, phone, birth_date, city, create_time) " +
            "VALUES (:fullName, :email, :login, :phone, :birthDate, :city, :now)"
    )
    @Timestamped
    @GetGeneratedKeys("id")
    UUID saveUser(@BindMethods User user);

    @SqlQuery(
            "SELECT id, fullName, email, login, phone, birthDate, city, imageId, createTime " +
            "FROM users " +
            "WHERE users.id = :id"
    )
    @RegisterConstructorMapper(User.class)
    User findById(@Bind("id") UUID id);

    @SqlUpdate(
            "UPDATE users " +
            "SET full_name = :fullName, email = :email, login = :login, phone = :phone, city = :city, birth_date = :birthDate " +
            "WHERE users.id = :id"
    )
    int updateUser(@Bind("id") UUID id, @BindBean UpdatePayload payload);

    @SqlUpdate(
            "UPDATE users " +
            "SET image_id = :imageId " +
            "WHERE users.id = :id"
    )
    int updateUserImage(@Bind("id") UUID id, @Bind("imageId") UUID imageId);

    @SqlQuery("SELECT COUNT(*) FROM users")
    long countRows();

    @SqlQuery("""
        SELECT id, full_name, email, login, phone, birth_date, city, image_id, create_time
        FROM users
        WHERE (:searchName IS NULL OR full_name ILIKE '%' || :searchName || '%')
          AND (:city IS NULL OR city = :city)
          AND birth_date BETWEEN :minBirthDate AND :maxBirthDate
        ORDER BY id
        LIMIT 20 OFFSET :offset
        """)
    @RegisterConstructorMapper(User.class)
    List<User> findAllWithParams(
            @BindBean SearchParams params,
            @Bind("minBirthDate") LocalDate minBirthDate,
            @Bind("maxBirthDate") LocalDate maxBirthDate,
            @Bind("offset") int offset
    );
}
