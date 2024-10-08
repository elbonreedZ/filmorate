package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddUserWithValidData() throws Exception {
        String validUserJson = "{"
                + "\"email\":\"valid.email@example.com\","
                + "\"login\":\"validLogin\","
                + "\"name\":\"Valid Name\","
                + "\"birthday\":\"2000-01-01\""
                + "}";

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddUserWithInvalidEmail() throws Exception {
        String invalidEmailUserJson = "{"
                + "\"email\":\"invalidemail@\","
                + "\"login\":\"validLogin\","
                + "\"name\":\"Valid Name\","
                + "\"birthday\":\"2000-01-01\""
                + "}";

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddUserWithBlankLogin() throws Exception {
        String blankLoginUserJson = "{"
                + "\"email\":\"validemail@example.com\","
                + "\"login\":\"\","
                + "\"name\":\"Valid Name\","
                + "\"birthday\":\"2000-01-01\""
                + "}";

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankLoginUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddUserWithFutureBirthday() throws Exception {
        String futureBirthdayUserJson = "{"
                + "\"email\":\"validemail@example.com\","
                + "\"login\":\"validLogin\","
                + "\"name\":\"Valid Name\","
                + "\"birthday\":\"2100-01-01\""
                + "}";

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(futureBirthdayUserJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}