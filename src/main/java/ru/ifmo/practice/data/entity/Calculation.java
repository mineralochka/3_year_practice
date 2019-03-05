package ru.ifmo.practice.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Calculation entity
 */
@Data
@Entity
@Table(name = "calculation")
public class Calculation {
    /**
     * Unique identifier
     */
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    @SequenceGenerator(sequenceName = "calculation_seq", name = "id_generator")
    private Long id;

    /**
     * user input
     */
    @Column
    private String input;

    /**
     * Calculation result
     */
    @Column
    private String result;

    /**
     * Calculation execution timestamp
     */
    @Column
    private LocalDateTime timestamp;

    /**
     * User's session
     */
    @Column
    private String sessionId;
}
