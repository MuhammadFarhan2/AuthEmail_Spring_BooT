package com.client.emailauth.service;

import com.client.emailauth.entity.UserEntity;
import com.client.emailauth.entity.VerificationToken;
import com.client.emailauth.model.UserModel;

import java.util.Optional;

public interface UserService {
    UserEntity registerUser(UserModel userModel);
    void saveVerificationTokenForUser(String token, UserEntity user);
    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    UserEntity findUserByEmail(String email);

    void createPasswordResetTokenForUser(UserEntity user, String token);


    String validatePasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    void changePassword(UserEntity user, String newPassword);

    boolean checkIfValidOldePassword(UserEntity user, String oldPassword);
}
