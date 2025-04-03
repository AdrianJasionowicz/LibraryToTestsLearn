package com.example.jasionowicz.Book;

import com.example.jasionowicz.Cart.BooksCart;
import com.example.jasionowicz.User.LibraryUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.apache.catalina.User;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String author;
    private Boolean isAvailable;
    private int borrowCount = 0;
    @ManyToOne
    @JsonIgnore
    private LibraryUser libraryUser;
    @ManyToMany(mappedBy = "books")
    @JsonIgnore
    private List<BooksCart> carts = new ArrayList<>();

}
