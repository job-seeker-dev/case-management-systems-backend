package com.main.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.main.models.Case;
import com.main.services.CaseService;

import java.util.List;

@RestController
@RequestMapping(value = "/case")
@Tag(name = "Case")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @Operation(summary = "This operation is used to create case details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Case created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @PostMapping(value = "/create-case", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<Case> createCaseDetails(@Valid @RequestBody Case caseDetails) {
        Case caseA = caseService.createCase(caseDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(caseA);
    }

    @Operation(summary = "This operation is used to update case details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Case updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Case not found"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @PatchMapping(value = "/update-case/{caseId}/{status}", produces = {"application/json"})
    public ResponseEntity<Case> updateCaseDetails(@PathVariable Long caseId,
                                                  @PathVariable String status) {
        return caseService.updateCaseStatus(caseId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "This operation is used to find a case")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the case"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Case not exist"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @GetMapping(value = "/find-case/{caseId}", produces = {"application/json"})
    public ResponseEntity<Case> findACase(@PathVariable(value = "caseId") Long caseId) {
        Case caseA = caseService.findCase(caseId);
        return ResponseEntity.status(HttpStatus.OK).body(caseA);
    }

    @Operation(summary = "This operation is used to find all cases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all cases"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @GetMapping(value = "/find-all-cases", produces = {"application/json"})
    public ResponseEntity<List<Case>> findAllUsers() {
        List<Case> caseList = caseService.findAllCases();
        return ResponseEntity.status(HttpStatus.OK).body(caseList);
    }

    @Operation(summary = "This operation is used to delete a case")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Case deleted, no content exist"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @DeleteMapping(value = "/{caseId}")
    public ResponseEntity<?> deleteCase(@PathVariable Long caseId) {
        caseService.deleteCase(caseId);
        return ResponseEntity.noContent().build();
    }
}
