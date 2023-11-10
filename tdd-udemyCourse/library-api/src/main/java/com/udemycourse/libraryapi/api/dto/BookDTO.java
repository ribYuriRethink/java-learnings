package com.udemycourse.libraryapi.api.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data  // gera os gettter and setters
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String isbn;
}
