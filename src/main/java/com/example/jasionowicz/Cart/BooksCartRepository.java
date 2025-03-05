package com.example.jasionowicz.Cart;

import com.example.jasionowicz.User.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BooksCartRepository extends JpaRepository<BooksCart, Integer > {
    Optional<BooksCart> findByLibraryUser(LibraryUser libraryUser);

    void deleteByLibraryUser(LibraryUser user);
}
