package com.main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import com.main.models.Case;
import com.main.repository.CaseRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.main.constants.CommonConstant.VALIDATION_MESSAGE_FOR_STATUS;
import static com.main.constants.CommonConstant.VALIDATION_MESSAGE_FOR_TITLE;
import static com.main.constants.ErrorsConstant.NO_SUCH_ELEMENT_EXCEPTION;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CaseControllerTest {

    public static final String BASE_URL_FOR_CREATE_CASE = "/case/create-case";
    public static final String BASE_URL_FOR_UPDATE_CASE = "/case/update-case/";
    public static final String BASE_URL_FOR_FIND_CASE = "/case/find-case/";
    public static final String BASE_URL_FOR_FIND_ALL_CASE = "/case/find-all-cases";
    public static final String BASE_URL_FOR_DELETE_CASE = "/case/";
    public static final Long CASE_ID = 12345L;
    public static final String STATUS = "Pending";
    public static final String STATUS_TO_BE_UPDATED = "Completed";
    public static final String TITLE = "Sample";
    public static final String DESCRIPTION = "Case description";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CaseRepository caseRepository;

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @Test
    public void testCreateCaseDetails() throws Exception {

        String caseJson = objectMapper.writeValueAsString(createCase());

        mockMvc.perform(post(BASE_URL_FOR_CREATE_CASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseId").exists())
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.status").value(STATUS));
    }

    @Test
    public void testCreateCaseWHenValidationErrorWithBadRequest() throws Exception {

        String invalidCaseJson = """
            {
              "title": ""
            }
            """;

        mockMvc.perform(post(BASE_URL_FOR_CREATE_CASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCaseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title")
                        .value(VALIDATION_MESSAGE_FOR_TITLE))
                .andExpect(jsonPath("$.errors.status")
                        .value(VALIDATION_MESSAGE_FOR_STATUS));
    }

    @Test
    public void testCreateCaseWHenInternalServerError() throws Exception {
        Case created = createCase();
        created.setCaseId(CASE_ID);
        String caseJson = objectMapper.writeValueAsString(created);

        mockMvc.perform(post(BASE_URL_FOR_CREATE_CASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson))
                .andExpect(status().isInternalServerError());
}

    @Test
    public void testUpdateCaseDetailsWhenIdFound() throws Exception {
        Case created = createCase();
        created = caseRepository.save(created);

        mockMvc.perform(patch(BASE_URL_FOR_UPDATE_CASE + created.getCaseId()
                        + "/" + STATUS_TO_BE_UPDATED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(STATUS_TO_BE_UPDATED));
    }

    @Test
    public void testUpdateCaseDetailsWhenIdNotFound() throws Exception {

        mockMvc.perform(patch(BASE_URL_FOR_UPDATE_CASE + CASE_ID
                        + "/" + STATUS_TO_BE_UPDATED))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindACaseWhenIdExist() throws Exception {
        Case created = createCase();
        created = caseRepository.save(created);

        mockMvc.perform(get(BASE_URL_FOR_FIND_CASE + created.getCaseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").exists())
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.status").value(STATUS));
    }

    @Test
    public void testFindACaseWhenIdNotExistShouldThrowNoSuchElement()
            throws Exception {
        mockMvc.perform(get(BASE_URL_FOR_FIND_CASE + CASE_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(NO_SUCH_ELEMENT_EXCEPTION)));
    }

    @Order(1)
    @Test
    public void testFindAllUsers() throws Exception {
        caseRepository.save(new Case(null, "Sample 1", "desc 1",
                "Pending", LocalDateTime.now().plusDays(1)));

        caseRepository.save(new Case(null, "Sample 2", "desc 2",
                "Completed", LocalDateTime.now().plusDays(2)));

        mockMvc.perform(get(BASE_URL_FOR_FIND_ALL_CASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteCase() throws Exception {
        Case created = createCase();
        created = caseRepository.save(created);

        mockMvc.perform(delete(BASE_URL_FOR_DELETE_CASE + created.getCaseId()))
                .andExpect(status().isNoContent());
    }

    private Case createCase() {
        Case cs = new Case();
        cs.setTitle(TITLE);
        cs.setDescription(DESCRIPTION);
        cs.setStatus(STATUS);
        cs.setDueDateTime(LocalDateTime.now().plusDays(7));
        return cs;
    }
}
