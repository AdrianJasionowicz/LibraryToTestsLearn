package com.example.jasionowicz.BorrowHistory;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.User.LibraryUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal fee = BigDecimal.valueOf(10.25).setScale(2, RoundingMode.HALF_UP);
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
