package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileUserView {


    private Integer id;
    private String name;
    private String email;
    private int accountBalance;
    private List<Book> borrowedBooks;
    private String newPassword;
    private String oldPassword;
}
