package com.client.emailauth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
public class PasswordResetToken {

    //    With 10 minute of time
    private static final  int EXPIRATION_TIME = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String token;
    //    we assigning the 10 to 15 minute for token to expire
    private Date expirationTime;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_TOKEN"))
    private UserEntity user;


//    Creating Construtor to pass the token to user


    public PasswordResetToken(String token) {
        this.token = token;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }

    public PasswordResetToken(UserEntity user, String token) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }

    private Date calculateExpirationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expirationTime);

        return new Date(calendar.getTime().getTime());
    }

}
