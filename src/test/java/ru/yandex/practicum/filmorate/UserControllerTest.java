package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_addFilmWithInvalidLogin() throws Exception {
        String content = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"email\": \"yandex@mail.ru\",\n" +
                "  \"birthday\": \"2446-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test2_addFilmWithInvalidEmail() throws Exception {
        String content = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"mail.ru\",\n" +
                "  \"birthday\": \"1980-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_addFilmWithInvalidBirthday() throws Exception {
        String content = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"mail.ru\",\n" +
                "  \"birthday\": \"1980-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test4_addFriendWitNotExistUser() throws Exception {

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNotFound());
    }

}