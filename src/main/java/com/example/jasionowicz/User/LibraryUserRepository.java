package com.example.jasionowicz.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryUserRepository extends JpaRepository<LibraryUser, Integer> {

    LibraryUser findByEmail(String email);
    Optional<LibraryUser> findById(int id);
}
