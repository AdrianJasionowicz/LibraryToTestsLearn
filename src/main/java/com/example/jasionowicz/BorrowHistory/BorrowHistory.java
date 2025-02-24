package com.example.jasionowicz.BorrowHistory;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.User.LibraryUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BorrowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private LibraryUser user;

    @ManyToOne
    private Book book;

    private LocalDate borrowDate;
    private LocalDate returnDate;
}
