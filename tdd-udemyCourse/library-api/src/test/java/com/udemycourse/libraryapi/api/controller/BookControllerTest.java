package com.udemycourse.libraryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemycourse.libraryapi.api.dto.BookDTO;
import com.udemycourse.libraryapi.api.entity.Book;
import com.udemycourse.libraryapi.api.exception.BusinessException;
import com.udemycourse.libraryapi.api.service.BookService;
import com.udemycourse.libraryapi.api.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    private final static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Should create a book with success.")
    public void createBook() throws Exception {
        BookDTO dto = createNewBookDto();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book savedBook = Book.builder().id(1L).author("Autor").title("As aventuras").isbn("1234").build();
        when(bookService.saveBook(dto)).thenReturn(savedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Should throw an validation error if the given data is not correct.")
    public void createInvalidBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Should throw an error if the book isbn already exists.")
    public void createBookWithDuplicatedIsbn() throws Exception {
        BookDTO dto = createNewBookDto();
        String json = new ObjectMapper().writeValueAsString(dto);
        String errorMessage = "Isbn ja cadastrado";

        when(bookService.saveBook(any(BookDTO.class))).thenThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    @DisplayName("Should return the book corresponding to the id")
    public void getBookDetails() throws Exception {
        Long id = 1L;
        BookDTO bookDto = createNewBookDto();

        Book book = buildBook();

        when(bookService.getById(anyLong())).thenReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(bookDto.getTitle()))
                .andExpect(jsonPath("author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDto.getIsbn()));
    }



    @Test
    @DisplayName("Should return  Not Found Status if there is no book to the given id")
    public void bookNotFound() throws Exception{
        when(bookService.getById(anyLong())).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return a status OK and the updated book with success")
    public void updateBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewBookDto());
        Book updatedBook = buildBook();

        when(bookService.updateBook(anyLong(), any(BookDTO.class))).thenReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(updatedBook.getId()))
                .andExpect(jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(jsonPath("author").value(updatedBook.getAuthor()))
                .andExpect(jsonPath("isbn").value(updatedBook.getIsbn()));
    }

    @Test
    @DisplayName("Should return status Not Found on update a nonexistent book ")
    public void updateNonExistentBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewBookDto());
        when(bookService.updateBook(anyLong(), any())).thenReturn(null);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return status No Content on delete a book with success")
    public void deleteBook() throws Exception{
        when(bookService.deleteBook(anyLong())).thenReturn(1L);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return status Not Found on delete a nonexistent book")
    public void deleteNonExistentBook() throws Exception{
        when(bookService.deleteBook(anyLong())).thenReturn(null);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter books by the given params")
    public void findBookTest () throws Exception {
        Book book = buildBook();

        when(bookService.find(any(BookDTO.class), any(Pageable.class)))
                .thenReturn( new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private static BookDTO createNewBookDto() {
        return BookDTO.builder().author("Autor").title("As aventuras").isbn("1234").build();
    }
    private static Book buildBook() {
        return Book.builder()
                .id(1L)
                .title("As aventuras")
                .author("Autor")
                .isbn("1234")
                .build();
    }

}
