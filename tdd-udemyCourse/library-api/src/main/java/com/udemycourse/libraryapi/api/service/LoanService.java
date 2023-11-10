package com.udemycourse.libraryapi.api.service;

import com.udemycourse.libraryapi.api.dto.LoanDto;
import com.udemycourse.libraryapi.api.dto.ReturnedLoanDto;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.entity.Loan;
import com.udemycourse.libraryapi.api.exception.BusinessException;
import com.udemycourse.libraryapi.api.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(BookService bookService, LoanRepository loanRepository) {
        this.bookService = bookService;
        this.loanRepository = loanRepository;
    }

    public Loan save(LoanDto loanDto) {
        Optional<Book> bookByIsbn = bookService.getBookByIsbn(loanDto.getIsbn());
        if (bookByIsbn.isEmpty()) throw new BusinessException("The book for the given id does not exists!");
        if (loanRepository.existsByBookAndNotReturned(bookByIsbn.get()))
            throw new BusinessException("The book is already loaned!");

        Loan loan = new Loan();
        loan.setCustomer(loanDto.getCustomer());
        loan.setCustomerEmail(loanDto.getEmail());
        loan.setBook(bookByIsbn.get());
        loan.setReturned(false);
        loan.setLoanDate(LocalDate.now());

        return loanRepository.save(loan);
    }

    public Loan update(Long id, ReturnedLoanDto dto) {
        Optional<Loan> foundLoan = loanRepository.findById(id);
        if (foundLoan.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan does not exist!");

        foundLoan.get().setReturned(dto.isReturned());
        return loanRepository.save(foundLoan.get());
    }

    public Page<Loan> find(LoanDto loanDto, Pageable pageable) {
        return loanRepository.findByBookIsbnOrCustomer(loanDto.getIsbn(), loanDto.getCustomer(), pageable);
    }

    public Page<Loan> getLoansByBook(Long id, Pageable pageable) {
        Optional<Book> foundBook = bookService.getById(id);
        if (foundBook.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no book!");

        return loanRepository.findByBook(foundBook, pageable);
    }

    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
        return loanRepository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

}
