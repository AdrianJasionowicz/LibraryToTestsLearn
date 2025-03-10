package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.Config.LoginBase.LoginUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class LibraryUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private int accountBalance = 0;
    @OneToMany
    private List<Book> borrowedBooks;
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "login_user_id")
    private LoginUser loginUser;





}
