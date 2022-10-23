package com.client.emailauth.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserEntity {

//    Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private String email;

//    length of user's password is 60
    @Column(length = 60)
    private String password;
//    Assigning the role to user while registrating!
    private String role;
//    When user will verify by email then user account will be activated
    private boolean enable =false;

}
