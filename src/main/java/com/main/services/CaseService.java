package com.main.services;

import com.main.models.Case;

import java.util.List;
import java.util.Optional;

public interface CaseService {
    Case createCase(Case caseDetails);
    Optional<Case> updateCaseStatus(Long caseId, String status);
    Case findCase(Long caseId);
    List<Case> findAllCases();
    void deleteCase(Long caseId);
}
