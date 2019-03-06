package ru.ifmo.practice.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ifmo.practice.ApplicationTest;
import ru.ifmo.practice.data.entity.Calculation;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {ApplicationTest.class})
@TestPropertySource("classpath:application-test.properties")
public class CalculationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CalculationRepository calculationRepository;

    @Test
    public void test() {
        ru.ifmo.practice.data.entity.Calculation calculation = new Calculation();
        calculation.setInput("input");
        calculation.setResult("result");
        calculation.setTimestamp(LocalDateTime.now());
        calculation.setSessionId("sessionId");
        entityManager.persist(calculation);

        reflectionEquals(calculation, calculationRepository.findBySessionId("sessionId"), "id");
    }
}