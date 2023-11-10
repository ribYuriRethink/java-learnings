package com.udemycourse.libraryapi.api.service;

import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.exception.BusinessException;
import com.udemycourse.libraryapi.api.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(BookDTO bookDTO) {
        Book book = copyPropertiesToBook(bookDTO);
        if (bookRepository.existsByIsbn(book.getIsbn())) throw new BusinessException("Isbn ja existe.");

        return bookRepository.save(book);
    }


    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    public Book updateBook(Long id, BookDTO bookDTO) {
        Optional<Book> bookResult = getById(id);
        if (bookResult.isPresent()) {
            Book book = copyPropertiesToBook(bookDTO);
            book.setId(id);
            bookRepository.save(book);
            return book;
        }
        return null;
    }

    public Long deleteBook(Long id) {
        Optional<Book> book = getById(id);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return id;
        }
        return null;
    }


    public Page<Book> find(BookDTO bookDTO, Pageable pageable) {
        Book bookFilter = copyPropertiesToBook(bookDTO);
        Example<Book> example = Example.of(bookFilter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return bookRepository.findAll(example, pageable);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    private static Book copyPropertiesToBook(BookDTO bookDTO) {
        var book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        return book;
    }
}
