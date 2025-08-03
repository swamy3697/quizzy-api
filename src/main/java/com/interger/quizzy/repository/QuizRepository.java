package com.interger.quizzy.repository;

import com.interger.quizzy.model.OptionDTO;
import com.interger.quizzy.model.requests.QuestionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean save(int creatorId, QuestionCreateRequest req) {
        String sql = """
            WITH 
            q_insert AS (
              INSERT INTO questions(user_id, question, refer_to, description, level, type)
              VALUES (?, ?, ?, ?, ?, ?)
              RETURNING question_id
            ),
            tag_insert AS (
                       INSERT INTO tags (tag, count, hotness_score)
                       SELECT UNNEST(?::citext[]), 1, 1
                       ON CONFLICT (tag) DO UPDATE
                       SET
                         count = tags.count + 1,
                         hotness_score = tags.hotness_score + 1
                       RETURNING tag_id
            ),
            question_tag_insert AS (
              INSERT INTO question_tags(question_id, tag_id)
              SELECT 
                (SELECT question_id FROM q_insert),
                tag_id
              FROM tag_insert
            ),
            options_insert AS (
              INSERT INTO options(question_id, option, is_correct)
              SELECT 
                (SELECT question_id FROM q_insert),
                opt, correct
              FROM UNNEST(?::text[], ?::boolean[]) AS t(opt, correct)
            ),
            leaderboard_upsert AS (
              INSERT INTO leaderboard(user_id, total_score, last_updated_at)
              VALUES (?, 10, now())
              ON CONFLICT (user_id)
              DO UPDATE SET 
                total_score = leaderboard.total_score + 10,
                last_updated_at = now()
            )
            SELECT TRUE;
        """;

        // Convert data
        List<String> optionTexts = req.getOptions().stream()
                .map(OptionDTO::getOption)
                .toList();
        List<Boolean> isCorrectList = req.getOptions().stream()
                .map(OptionDTO::isCorrect)
                .toList();
        List<String> tags = req.getTags();

        Object[] args = {
                creatorId,                              // questions.user_id
                req.getQuestion(),                      // questions.question
                req.getReferTo(),                       // questions.refer_to
                req.getDescription(),                   // questions.description
                mapLevel(req.getLevel()),               // questions.level (int)
                req.getType(),                          // questions.type

                toSqlArray("citext", tags),             // tags array
                toSqlArray("text", optionTexts),        // options.option
                toSqlArray("boolean", isCorrectList),   // options.is_correct

                creatorId                               // leaderboard.user_id
        };

        try {
            Boolean result = jdbcTemplate.queryForObject(sql, args, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            e.printStackTrace(); // Or use logger
            return false;
        }
    }

    private int mapLevel(String level) {
        return switch (level.toLowerCase()) {
            case "easy" -> 1;
            case "medium" -> 2;
            case "hard" -> 3;
            case "god level" -> 4;
            default -> throw new IllegalArgumentException("Invalid level: " + level);
        };
    }


    private java.sql.Array toSqlArray(String pgType, List<?> list) {
        try {
            return jdbcTemplate.getDataSource()
                    .getConnection()
                    .createArrayOf(pgType, list.toArray());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
