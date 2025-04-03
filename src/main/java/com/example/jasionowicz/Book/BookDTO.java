package com.example.jasionowicz.Book;

import com.example.jasionowicz.User.LibraryUser;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookDTO {

    private Integer id;
    private String title;
    private String author;
    private Boolean isAvailable;
    private int borrowCount;
    private LibraryUser libraryUserDTO;



}
