package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    public void testAddFilmWithValidData() throws Exception {
        String validFilmJson = "{"
                + "\"name\":\"Valid Film\","
                + "\"description\":\"Valid description\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\":\"PT2H30M\""
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validFilmJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() {
        Film film =  Film.builder().name("Valid Name").description("Valid description")
                .duration(Duration.ofMinutes(200)).releaseDate(LocalDate.of(1785, 12,5))
                .build();
        FilmController filmController = new FilmController();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    public void testAddFilmWithNegativeDuration() {
        Film film =  Film.builder().name("Valid Name").description("Valid description")
                .duration(Duration.ofMinutes(-200)).releaseDate(LocalDate.of(1900, 12,5))
                .build();
        FilmController filmController = new FilmController();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    public void testAddFilmWithBlankName() throws Exception {
        String emptyNameFilmJson = "{"
                + "\"name\":\"\","
                + "\"description\":\"Valid description\","
                + "\"releaseDate\":\"2022-01-01\","
                + "\"duration\":\"PT2H30M\""
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
                + "\"duration\":\"PT2H30M\""
                + "}";

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDescriptionLengthFilmJson))
                .andExpect(status().isBadRequest());
    }
}
