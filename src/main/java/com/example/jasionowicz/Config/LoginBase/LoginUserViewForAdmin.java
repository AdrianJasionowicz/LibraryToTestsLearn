package com.example.jasionowicz.Config.LoginBase;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserViewForAdmin {

    private Long id;
    private String username;
    private LoginUserRole role;
    private Integer LibraryUserId;
    private String name;
    private String email;
}
