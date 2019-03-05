package ru.ifmo.practice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ifmo.practice.service.CalculationService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CalculationController {
    private final CalculationService calculationService;

    @Autowired
    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Run a calculation
     *
     * @param session user session
     * @param request user request
     * @return calculation result
     */
    @PostMapping("/calculate")
    public Calculation calculate(HttpSession session, @RequestBody String request) {
        return calculationService.calculate(request, session);
    }

    /**
     * Get calculations list
     *
     * @param session user session
     * @return list of all past calculations
     */
    @GetMapping("/history")
    public List<Calculation> history(HttpSession session) {
        return calculationService.findBySession(session);
    }
}
