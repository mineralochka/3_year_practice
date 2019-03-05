package ru.ifmo.practice.data.repository;

import org.springframework.data.repository.CrudRepository;
import ru.ifmo.practice.data.entity.Calculation;

import java.util.List;

/**
 * Repository for {@link Calculation}
 */
public interface CalculationRepository extends CrudRepository<Calculation, Long> {

    List<Calculation> findBySessionId(String sessionId);
}
