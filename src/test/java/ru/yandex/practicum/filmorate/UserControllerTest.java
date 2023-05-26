package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
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

        mockMvc.perform(put("/users/1/friends/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test5_addNormalUser() throws Exception {
        addUser1(mockMvc);
    }

    @Test
    void test6_addFriend() throws Exception {
        addUser1(mockMvc);
        addUser2(mockMvc);

        mockMvc.perform(put("/users/1/friends/2")
        ).andExpect(status().is2xxSuccessful());
    }

    void addUser1(MockMvc mockMvc) throws Exception {
        String content = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        mockMvc.perform(post("/users")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isOk());
    }

    void addUser2(MockMvc mockMvc) throws Exception {
        String content = "{\n" +
                "  \"login\": \"dolore2\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail1@mail.ru\",\n" +
                "  \"birthday\": \"1970-08-20\"\n" +
                "}";
        mockMvc.perform(post("/users")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isOk());
    }

}