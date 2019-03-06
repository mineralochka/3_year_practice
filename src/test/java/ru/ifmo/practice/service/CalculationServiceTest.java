package ru.ifmo.practice.service;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import ru.ifmo.practice.data.entity.Calculation;
import ru.ifmo.practice.data.repository.CalculationRepository;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CalculationServiceTest {
    private static final HttpSession HTTP_SESSION = new MockHttpSession();
    private static final String SESSION_ID = HTTP_SESSION.getId();
    private static final String INPUT = "input";
    private static final String VALID_INPUT = "1+1";
    private static final String OUTPUT = "output";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String VALID_RESPONSE = "2";

    @Mock
    private CalculationRepository calculationRepository;

    private CalculationService target;

    @ClassRule
    public static ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        target = new CalculationService(calculationRepository);

        Calculation calc = new Calculation();
        calc.setSessionId(SESSION_ID);
        calc.setInput(INPUT);
        calc.setResult(OUTPUT);
        calc.setTimestamp(TIMESTAMP);
        when(calculationRepository.findBySessionId(SESSION_ID)).thenReturn(Collections.singletonList(calc));
    }

    @Test
    public void calculateOK() {
        Calculation expected = new Calculation();
        expected.setSessionId(SESSION_ID);
        expected.setInput(VALID_INPUT);
        expected.setResult(VALID_RESPONSE);

        ru.ifmo.practice.rest.Calculation result = target.calculate(VALID_INPUT, HTTP_SESSION);
        verify(calculationRepository).save(refEq(expected, "timestamp"));

        assertEquals("2", result.getResponse());
    }

    @Test
    public void findBySession() {
        List<ru.ifmo.practice.rest.Calculation> c9ns = target.findBySession(HTTP_SESSION);
        assertEquals(1, c9ns.size());
        assertThat(c9ns, hasItem(
                allOf(
                        hasProperty("request", is(INPUT)),
                        hasProperty("response", is(OUTPUT)),
                        hasProperty("timestamp", is(TIMESTAMP))
                )
        ));
    }
}