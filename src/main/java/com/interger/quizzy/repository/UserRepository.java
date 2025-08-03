package com.interger.quizzy.repository;

import com.interger.quizzy.model.User;
import com.interger.quizzy.repository.row_mappers.UserRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;


    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public long save(User user) {
        String sql = """
            INSERT INTO users (full_name, email, user_name, password_hash, avatar, created_at)
            VALUES (?, ?, ?, ?, ?, now())
            RETURNING user_id
        """;

        return jdbcTemplate.queryForObject(sql, Long.class,
                user.getFullName(),
                user.getEmail(),
                user.getUserName(),
                user.getPasswordHash(),
                user.getAvatar()
        );
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public User getUserByUserEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public User getUserByUserName(String username) {
        String sql = "SELECT * FROM users WHERE user_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public void updateAvatar(String email, String avatar) {
        jdbcTemplate.update(
                "UPDATE users SET avatar = ? WHERE email = ?",
                avatar, email
        );
    }

    public User insertOAuthUser(String email, String username, String fullName, String avatar, String oauthId, String oauthProvider) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO users (email, user_name, full_name, avatar, oauth_id, oauthprovider, password_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NULL) RETURNING *",
                new UserRowMapper(),
                email, username, fullName, avatar, oauthId, oauthProvider
        );
    }

    public Optional<User> getUserByUserId(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

