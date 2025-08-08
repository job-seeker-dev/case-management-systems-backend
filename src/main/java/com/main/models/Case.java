package com.main.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.main.constants.CommonConstant.VALIDATION_MESSAGE_FOR_STATUS;
import static com.main.constants.CommonConstant.VALIDATION_MESSAGE_FOR_TITLE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "case_details")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    @NotBlank(message = VALIDATION_MESSAGE_FOR_TITLE)
    @Column(nullable = false)
    private String title;

    private String description;

    @NotBlank(message = VALIDATION_MESSAGE_FOR_STATUS)
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dueDateTime;
}
