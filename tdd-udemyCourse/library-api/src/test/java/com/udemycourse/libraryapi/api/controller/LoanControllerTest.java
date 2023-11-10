package com.udemycourse.libraryapi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.dto.LoanDto;
import com.udemycourse.libraryapi.api.dto.ReturnedLoanDto;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.entity.Loan;
import com.udemycourse.libraryapi.api.exception.BusinessException;
import com.udemycourse.libraryapi.api.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    private final String LOAN_API = "/api/loans";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Should permorf a loan")
    public void createLoan() throws Exception {
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Loan loan = getLoan();
        when(loanService.save(ArgumentMatchers.any(LoanDto.class))).thenReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
    }

    @Test
    @DisplayName("Should throw an error if the book does not exists")
    public void createLoanFail() throws Exception {
        LoanDto dto = LoanDto.builder().isbn("000").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        when(loanService.save(ArgumentMatchers.any(LoanDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for the given isbn!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for the given isbn!"));
    }

    @Test
    @DisplayName("Should throw an error if the book is already loaned")
    public void loanedBookOnCreate() throws Exception {
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        when(loanService.save(ArgumentMatchers.any(LoanDto.class)))
                .thenThrow(new BusinessException("The Book is already loaned!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("The Book is already loaned!"));
    }

    @Test
    @DisplayName("Should update the returned property of the loan")
    public void returnBook() throws Exception {
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        when(loanService.update(anyLong(), any(ReturnedLoanDto.class))).thenReturn(getLoan());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        verify(loanService, times(1)).update(anyLong(), any(ReturnedLoanDto.class));
    }

    @Test
    @DisplayName("Should throw an error if the loan does not exist and return Not Found status code")
    public void returnBookFail() throws Exception {
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        when(loanService.update(anyLong(), any(ReturnedLoanDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan does not exist!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).update(anyLong(), any(ReturnedLoanDto.class));
    }

    @Test
    @DisplayName("Should filter books by the given params")
    public void findBookTest () throws Exception {
        Loan loan = getLoan();

        when(loanService.find(any(LoanDto.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100", loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private static Loan getLoan() {
        Book book = Book.builder().id(1L).isbn("123").build();
        return Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).returned(false).build();
    }
}