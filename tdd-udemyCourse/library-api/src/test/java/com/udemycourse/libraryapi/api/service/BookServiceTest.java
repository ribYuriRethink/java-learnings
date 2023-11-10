package com.udemycourse.libraryapi.api.service;

import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BookService.class)
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @Test
    @DisplayName("Should return the saved book")
    public void saveBook() {
        BookDTO bookDto = createNewBookDto();
        Book book = Book.builder().id(10L).author("Autor").title("Meu Livro").isbn("2131").build();

        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book savedBook = bookService.saveBook(bookDto);

        assertEquals(10L, savedBook.getId());
    }

    @Test
    @DisplayName("Should throw an error if the book isbn already exists")
    public void savingBookWithDuplicatedIsbn() {
        BookDTO bookDto = createNewBookDto();
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);
        try {
            bookService.saveBook(bookDto);
        } catch (Exception e) {
            assertEquals("Isbn ja existe.", e.getMessage());
            verify(bookRepository, Mockito.never()).save(any(Book.class));
        }
    }

    @Test
    @DisplayName("Should get the book by id")
    public void getBookById() {
        Book book = createNewBook();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = bookService.getById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals(book.getId(), foundBook.get().getId());
    }

    @Test
    @DisplayName("Should return empty when there is no book with the given id")
    public void getBookByIdFail() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Book> foundBook = bookService.getById(1L);
        assertTrue(foundBook.isEmpty());
    }

    @Test
    @DisplayName("Should return the updated book if exists on database")
    public void updateBookSuccess() {
        Book book = createNewBook();
        BookDTO updateBookDto = createNewBookDto();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book updatedBook = bookService.updateBook(1L, updateBookDto);

        assertNotNull(updatedBook);
        assertEquals(updateBookDto.getTitle(), updatedBook.getTitle());
    }

    @Test
    @DisplayName("Should return null if the book does not exists on database")
    public void updateBookFail() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Book updatedBook = bookService.updateBook(1L, null);
        assertNull(updatedBook);
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should return the id of the deleted book")
    public void deleteBookSuccess() {
        Book book = createNewBook();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Long deletedBookId = bookService.deleteBook(1L);

        assertEquals(1L, deletedBookId);
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should return null if there is no book with the given id")
    public void deleteBookFail() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Long deletedBookId = bookService.deleteBook(1L);
        assertNull(deletedBookId);
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should filter by properties")
    public void findBook() {
        Book book = createNewBook();
        BookDTO bookDTO = createNewBookDto();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> bookList = Arrays.asList(book);
        Page<Book> booksPage = new PageImpl<>(bookList, pageRequest, 1);

        when(bookRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(booksPage);

        Page<Book> result = bookService.find(bookDTO, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(bookList, result.getContent());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(10, result.getPageable().getPageSize());
    }

    @Test
    @DisplayName("Should return the book by the given isbn")
    public void getBookByIsbn() {
        String isbn = "2131";
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(createNewBook()));
        Optional<Book> bookByIsbn = bookService.getBookByIsbn(isbn);

        assertTrue(bookByIsbn.isPresent());
        assertEquals(1L, bookByIsbn.get().getId());
        assertEquals("2131", bookByIsbn.get().getIsbn());
        verify(bookRepository, times(1)).findByIsbn(anyString());
    }

    private static BookDTO createNewBookDto() {
        return BookDTO.builder().author("Autor").title("Outro Livro").isbn("2131").build();
    }
    private static Book createNewBook() {
        return Book.builder().id(1L).author("Autor").title("Meu Livro").isbn("2131").build();
    }
}
