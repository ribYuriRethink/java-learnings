package com.udemycourse.libraryapi.api.service;

import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.dto.LoanDto;
import com.udemycourse.libraryapi.api.dto.ReturnedLoanDto;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.entity.Loan;
import com.udemycourse.libraryapi.api.exception.BusinessException;
import com.udemycourse.libraryapi.api.repository.LoanRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LoanService.class)
public class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanRepository loanRepository;

    @Test
    @DisplayName("Should save an loan")
    public void saveLoan() {
        LoanDto loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();
        Loan loan = createNewLoan();

        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(createNewBook()));
        when(loanRepository.existsByBookAndNotReturned(any(Book.class))).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan savedLoan = loanService.save(loanDto);
        assertEquals(1L, savedLoan.getId());
        assertEquals("123", savedLoan.getBook().getIsbn());
        verify(bookService, times(1)).getBookByIsbn(anyString());
    }

    @Test
    @DisplayName("Should throw an error if the book does not exist")
    public void saveLoanBookFail() {
        LoanDto loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.empty());

        try {
            loanService.save(loanDto);
            Assertions.fail();
        } catch (Exception e) {
            assertEquals(BusinessException.class, e.getClass());
            assertEquals("The book for the given id does not exists!", e.getMessage());
            verify(bookService, times(1)).getBookByIsbn(anyString());
            verify(loanRepository, times(0)).existsByBookAndNotReturned(any(Book.class));
        }
    }

    @Test
    @DisplayName("Should throw an error if the book is already loaned")
    public void saveBookLoaned() {
        LoanDto loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(createNewBook()));
        when(loanRepository.existsByBookAndNotReturned(any(Book.class))).thenReturn(true);

        try {
            loanService.save(loanDto);
            Assertions.fail();
        } catch (Exception e) {
            assertEquals(BusinessException.class, e.getClass());
            assertEquals("The book is already loaned!", e.getMessage());
            verify(bookService, times(1)).getBookByIsbn(anyString());
            verify(loanRepository, times(1)).existsByBookAndNotReturned(any(Book.class));
        }
    }

    @Test
    @DisplayName("Should update an Loan by the given id")
    public void updateLoan() {
        Long id = 1L;
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();
        Loan loan = createNewLoan();

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan updatedLoan = loanService.update(id, returnedLoanDto);

        assertTrue(updatedLoan.getReturned());
        assertEquals(loan.getId(), updatedLoan.getId());
        verify(loanRepository, times(1)).findById(anyLong());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw and error when there is no Loan with the given id")
    public void updateLoanFail() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        try {
            loanService.update(1L, new ReturnedLoanDto());
            Assertions.fail();
        } catch (Exception e) {
            assertEquals(ResponseStatusException.class, e.getClass());
            verify(loanRepository, times(1)).findById(anyLong());
        }
    }

    @Test
    @DisplayName("Should filter by properties")
    public void findBook() {
        Loan loan = createNewLoan();
        LoanDto loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> loanList = Arrays.asList(loan);
        Page<Loan> loanPage = new PageImpl<>(loanList, pageRequest, 1);

        when(loanRepository.findByBookIsbnOrCustomer(anyString(), anyString(), any(PageRequest.class))).thenReturn(loanPage);

        Page<Loan> result = loanService.find(loanDto, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(loanList, result.getContent());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(10, result.getPageable().getPageSize());
    }

    private static Loan createNewLoan() {
        return Loan.builder().id(1L).customer("Fulano").book(createNewBook()).returned(false).loanDate(LocalDate.now()).build();
    }

    private static Book createNewBook() {
        return Book.builder().id(1L).author("Autor").title("Meu Livro").isbn("123").build();
    }

}