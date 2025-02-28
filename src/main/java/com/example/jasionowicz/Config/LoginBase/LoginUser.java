package com.example.jasionowicz.Config.LoginBase;
import com.example.jasionowicz.User.LibraryUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class LoginUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    @OneToOne(mappedBy = "loginUser", cascade = CascadeType.ALL)
    private LibraryUser libraryUser;

}
