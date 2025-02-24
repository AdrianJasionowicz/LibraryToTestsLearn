package com.example.jasionowicz.Book;

import com.example.jasionowicz.User.LibraryUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.apache.catalina.User;

import java.sql.Time;
import java.time.LocalDate;

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
    @Column(name = "borrowed_by_user_id")
    private Integer borrowedByUserId;
    private int borrowCount = 0;
    @ManyToOne
    private LibraryUser libraryUser;



    public boolean isAvailable() {

        if ( isAvailable == null ) {
            return true;
        }
        return isAvailable;
    }

    public void increaseBorrowCount() {
        this.borrowCount++;
    }

}
