package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testAddFilmWithValidData() throws Exception {
        String validFilmJson = "{"
                + "\"name\":\"Valid Film\","
                + "\"description\":\"Valid description\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\": 12"
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validFilmJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() throws Exception {
        String invalidReleaseDateFilmJson = "{ \"name\": \"Valid Name\", \"description\": \"Valid description\", \"duration\": 12, \"releaseDate\": \"1785-12-05\" }";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(invalidReleaseDateFilmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddFilmWithNegativeDuration() throws Exception {
        String emptyNameFilmJson = "{"
                + "\"name\":\"Valid Name\","
                + "\"description\":\"Valid description\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\":-12"
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyNameFilmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddFilmWithBlankName() throws Exception {
        String emptyNameFilmJson = "{"
                + "\"name\":\"\","
                + "\"description\":\"Valid description\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\":\"30\""
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyNameFilmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddFilmWithInvalidDescriptionLength() throws Exception {
        String invalidDescriptionLengthFilmJson = "{"
                + "\"name\":\"Valid Film\","
                + "\"description\":\"А ещё явные признаки " +
                "победы институционализации являются только методом политического участия и преданы социально-демократической анафеме. Есть над чем задуматься: многие известные личности представлены в исключительно положительном свете.\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\":\"20\""
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDescriptionLengthFilmJson))
                .andExpect(status().isBadRequest());
    }
}
