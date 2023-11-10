package com.udemycourse.libraryapi.api.controller;

import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.entity.Loan;
import com.udemycourse.libraryapi.api.service.BookService;
import com.udemycourse.libraryapi.api.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    private final BookService bookService;
    private final LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get the book details by the id ") // swagger openapi
    public ResponseEntity<Object> get(@PathVariable Long id) {
        log.info("Getting a book by the id: {}", id);
        Optional<Book> book = bookService.getById(id);
        return book.isPresent()
                ? ResponseEntity.ok().body(book)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody @Valid BookDTO bookDTO) {
        log.info("Creating a book with the data: {}", bookDTO);
        return bookService.saveBook(bookDTO);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid BookDTO bookDTO) {
        Book updatedBook = bookService.updateBook(id, bookDTO);
        return updatedBook != null
                ? ResponseEntity.ok().body(updatedBook)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Long idDeleted = bookService.deleteBook(id);
        return idDeleted != null
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(idDeleted)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping
    public Page<Book> find(BookDTO bookDTO, Pageable pageable) {
        return bookService.find(bookDTO, pageable);
    }

    @GetMapping("{id}/loans")
    public Page<Loan> loansByBook(@PathVariable Long id, Pageable pageable) {
        return loanService.getLoansByBook(id, pageable);
    }

}
