package com.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.main.models.Case;
import com.main.repository.CaseRepository;
import com.main.services.CaseService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.main.constants.ErrorsConstant.NO_SUCH_ELEMENT_EXCEPTION;

@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;

    @Transactional
    @Override
    public Case createCase(Case caseDetails) {
        return caseRepository.save(caseDetails);
    }

    @Transactional
    @Override
    public Optional<Case> updateCaseStatus(Long caseId, String status) {
        return caseRepository.findById(caseId)
                .map(cs -> {
                    cs.setStatus(status);
                    return caseRepository.save(cs);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Case findCase(Long caseId) {
        return caseRepository.findById(caseId).orElseThrow(
                ()-> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Case> findAllCases() {
        return caseRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteCase(Long caseId) {
        caseRepository.deleteById(caseId);
    }
}
