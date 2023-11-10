package com.udemycourse.libraryapi.api.controller;

import com.udemycourse.libraryapi.api.dto.LoanDto;
import com.udemycourse.libraryapi.api.dto.ReturnedLoanDto;
import com.udemycourse.libraryapi.api.entity.Loan;
import com.udemycourse.libraryapi.api.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    public final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LoanDto loanDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.save(loanDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto dto) {
            return ResponseEntity.status(HttpStatus.OK).body(loanService.update(id, dto));
    }

    @GetMapping
    public Page<Loan> find(LoanDto loanDto, Pageable pageRequest) {
        return loanService.find(loanDto, pageRequest);
    }
}
