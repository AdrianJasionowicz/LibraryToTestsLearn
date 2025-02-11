package com.example.jasionowicz.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {
}
