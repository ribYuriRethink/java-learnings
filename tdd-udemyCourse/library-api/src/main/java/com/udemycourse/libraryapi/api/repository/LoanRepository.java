package com.udemycourse.libraryapi.api.repository;

import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "select case when ( count(l.id) > 0) then true else false end" +
            " from Loan l where l.book = :book and (l.returned = null or l.returned = false) ")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query(value = "select l from Loan as l join l.book as b " +
            "where b.isbn = :isbn or l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageRequest);

    Page<Loan> findByBook(Optional<Book> foundBook, Pageable pageable);

    @Query(value = "select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned = null or l.returned = false)")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}

