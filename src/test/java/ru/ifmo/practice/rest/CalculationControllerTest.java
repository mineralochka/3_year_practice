package ru.ifmo.practice.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.ifmo.practice.ApplicationTest;
import ru.ifmo.practice.service.CalculationService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ApplicationTest.class})
@WebMvcTest(CalculationController.class)
public class CalculationControllerTest {

    private static final String RESPONSE = "response";
    private static final String REQUEST = "request";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CalculationService calculationService;

    @Before
    public void setUp() {
        Calculation history = new Calculation();
        history.setResponse(RESPONSE);
        history.setRequest(REQUEST);
        history.setTimestamp(TIMESTAMP);
        when(calculationService.findBySession(any(HttpSession.class))).thenReturn(Collections.singletonList(history));

        Calculation live = new Calculation();
        live.setResponse(RESPONSE);
        when(calculationService.calculate(eq(REQUEST), any(HttpSession.class))).thenReturn(live);
    }

    @Test
    public void calculate() throws Exception {
        mvc.perform(post("/calculate")
                .content(REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", is(RESPONSE)));

    }

    @Test
    public void history() throws Exception {
        mvc.perform(get("/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].request", is(REQUEST)))
                .andExpect(jsonPath("$[0].response", is(RESPONSE)))
                .andExpect(jsonPath("$[0].timestamp", is(TIMESTAMP.toString())));
    }
}