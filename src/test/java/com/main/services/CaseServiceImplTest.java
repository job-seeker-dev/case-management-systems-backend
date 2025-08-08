package com.main.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.main.models.Case;
import com.main.repository.CaseRepository;
import com.main.services.impl.CaseServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static com.main.constants.ErrorsConstant.NO_SUCH_ELEMENT_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class CaseServiceImplTest {

    public static final Long CASE_ID = 1001L;
    public static final Long CASE_ID_NOT_EXIST = 2001L;
    public static final String STATUS = "Pending";
    public static final String STATUS_TO_BE_UPDATED = "Completed";
    public static final String TITLE = "Sample";
    public static final String DESCRIPTION = "Case description";

    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseServiceImpl caseService;

    @BeforeEach
    void setUp() {
        caseService = new CaseServiceImpl(caseRepository);
    }

    @Test
    void testCreateCase() {
        Case caseA = createCase();
        when(caseRepository.save(any(Case.class))).thenReturn(caseA);

        Case created = caseService.createCase(caseA);
        assertEquals(TITLE, created.getTitle());
        assertEquals(caseA, created);
        verify(caseRepository, times(1)).save(caseA);
    }

    @Test
    void testUpdateCaseStatusWhenIdFound() {
        Case caseA = createCase();
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseA));
        when(caseRepository.save(any(Case.class))).thenReturn(caseA);

        Optional<Case> updated = caseService.updateCaseStatus(
                CASE_ID, STATUS_TO_BE_UPDATED);
        assertTrue(updated.isPresent());
        assertEquals(STATUS_TO_BE_UPDATED, updated.get().getStatus());

        verify(caseRepository, times(1)).findById(CASE_ID);
        verify(caseRepository, times(1)).save(caseA);
    }

    @Test
    void testUpdateCaseStatusWhenIdNotFound() {
        Case caseA = createCase();
        when(caseRepository.findById(CASE_ID_NOT_EXIST)).thenReturn(Optional.empty());

        Optional<Case> updated = caseService.updateCaseStatus(
                CASE_ID_NOT_EXIST, STATUS_TO_BE_UPDATED);
        assertFalse(updated.isPresent());

        verify(caseRepository, times(1))
                .findById(CASE_ID_NOT_EXIST);
    }

    @Test
    void testFindCaseWhenFoundById() {
        Case caseA = createCase();
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseA));

        Case result = caseService.findCase(CASE_ID);
        assertNotNull(result);
        assertEquals(STATUS, result.getStatus());

        verify(caseRepository, times(1)).findById(CASE_ID);
    }

    @Test
    public void testFindCaseWhenNotFoundByIdShouldThrowNoSuchElement() {
        when(caseRepository.findById(CASE_ID_NOT_EXIST)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            caseService.findCase(CASE_ID_NOT_EXIST);
        });
        assertEquals(NO_SUCH_ELEMENT_EXCEPTION, exception.getMessage());

        verify(caseRepository, times(1)).findById(CASE_ID_NOT_EXIST);
    }

    @Test
    public void testFindAllCases() {
        List<Case> caseList = Arrays.asList(
                new Case(1001L, "Sample 1", "desc 1",
                        "Pending", LocalDateTime.now().plusDays(1)),
                new Case(1002L, "Sample 2", "desc 2",
                        "Completed", LocalDateTime.now().plusDays(2))
        );

        when(caseRepository.findAll()).thenReturn(caseList);

        List<Case> result = caseService.findAllCases();
        assertEquals(2, result.size());
        assertEquals(caseList, result);

        verify(caseRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteCase() {
        doNothing().when(caseRepository).deleteById(CASE_ID);
        caseService.deleteCase(CASE_ID);
        verify(caseRepository, times(1)).deleteById(CASE_ID);
    }

    private Case createCase() {
        return new Case(
                CASE_ID,
                TITLE,
                DESCRIPTION,
                STATUS,
                LocalDateTime.now().plusDays(7)
        );
    }
}