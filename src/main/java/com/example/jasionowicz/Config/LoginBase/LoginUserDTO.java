package com.example.jasionowicz.Config.LoginBase;

import com.example.jasionowicz.User.LibraryUser;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDTO {



    private Long id;
    private String username;
    private String password;
    private LibraryUser libraryUser;

}
