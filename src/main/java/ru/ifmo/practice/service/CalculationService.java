package ru.ifmo.practice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.practice.data.repository.CalculationRepository;
import ru.ifmo.practice.exception.CalculationException;
import ru.ifmo.practice.rest.Calculation;
import ru.ifmo.practice.util.CalculationUtil;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Performs calculations, persists and fetches them
 * Calculations are tied to users via sessions
 *
 * @author r.goroshko
 */

@Service
@Slf4j
public class CalculationService {
    private final CalculationRepository calculationRepository;

    @Autowired
    public CalculationService(CalculationRepository calculationRepository) {
        this.calculationRepository = calculationRepository;
    }

    /**
     * Calculate, save result and return it
     *
     * @param input   expression to calculate
     * @param session user session
     * @return calculation result
     */
    public Calculation calculate(String input, HttpSession session) {
        ru.ifmo.practice.data.entity.Calculation calculation = new ru.ifmo.practice.data.entity.Calculation();

        calculation.setInput(input);
        calculation.setTimestamp(LocalDateTime.now());
        calculation.setSessionId(session.getId());

        String stringRes;
        try {
            stringRes = CalculationUtil.calculate(input);
        } catch (CalculationException e) {
            log.error("For input: {}", input, e);
            stringRes = e.getMessage();
        }
        calculation.setResult(stringRes);
        calculationRepository.save(calculation);

        Calculation result = new Calculation();
        result.setResponse(stringRes);
        return result;
    }

    /**
     * Find calculations by session
     *
     * @param session user session
     * @return list of found calculations
     */
    public List<Calculation> findBySession(HttpSession session) {
        return calculationRepository.findBySessionId(session.getId()).stream()
                .map(c -> {
                    Calculation resp = new Calculation();
                    resp.setRequest(c.getInput());
                    resp.setResponse(c.getResult());
                    resp.setTimestamp(c.getTimestamp());
                    return resp;
                }).collect(Collectors.toList());
    }
}
