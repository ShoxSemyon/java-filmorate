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
public class FilmControllerTest {
    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_addFilmWithBlankName() throws Exception {
        String content = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        mockMvc.perform(post("/films")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test2_addFilmWitInvalidDescription() throws Exception {
        String content = "{\n" +
                "  \"name\": \"Film name\",\n" +
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        mockMvc.perform(post("/films")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_addFilmWitInvalidReleaseDate() throws Exception {
        String content = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        mockMvc.perform(post("/films")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test4_addFilmWitInvalidDuration() throws Exception {
        String content = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Descrition\",\n" +
                "  \"releaseDate\": \"1980-03-25\",\n" +
                "  \"duration\": -200\n" +
                "}";

        mockMvc.perform(post("/films")
                        .header("Content-Type", "application/json")
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test5_addLikeWitNotExistFilm() throws Exception {


        mockMvc.perform(put("/films/2/like/1"))
                .andExpect(status().isNotFound());
    }
}
