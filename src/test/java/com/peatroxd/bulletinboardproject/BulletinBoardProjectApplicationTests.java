package com.peatroxd.bulletinboardproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BulletinBoardProjectApplicationTests extends AbstractPostgresContainerTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsAndLiquibaseSeedsCategories() {
        Integer categoriesCount = jdbcTemplate.queryForObject(
                "select count(*) from categories",
                Integer.class
        );

        assertThat(categoriesCount).isNotNull();
        assertThat(categoriesCount).isGreaterThan(0);
    }
}
