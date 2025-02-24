package com.example.jasionowicz.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findAllByBorrowedByUserId(Integer borrowedByUserId);
    List<Book> getAllByAuthor(String author);
    List<Book> getAllByTitle(String title);

}
