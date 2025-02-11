package com.example.jasionowicz.Book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private Boolean isAvailable;
    private Long borrowedByUserId;

    public boolean isAvailable() {

        if ( isAvailable == null ) {
            return true;
        }
        return isAvailable;
    }

}
