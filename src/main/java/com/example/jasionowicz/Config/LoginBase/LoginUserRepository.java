package com.example.jasionowicz.Config.LoginBase;

import com.example.jasionowicz.User.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<LoginUser, Long> {
    Optional<LoginUser> findByUsername(String username);

    LoginUser getReferenceByUsername(String username);
    LoginUser findByLibraryUser(LibraryUser libraryUser);
}
