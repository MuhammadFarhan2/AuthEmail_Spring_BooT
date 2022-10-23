package com.client.emailauth.service;

import com.client.emailauth.entity.PasswordResetToken;
import com.client.emailauth.entity.UserEntity;
import com.client.emailauth.entity.VerificationToken;
import com.client.emailauth.model.UserModel;
import com.client.emailauth.repository.PasswordResetTokenRepository;
import com.client.emailauth.repository.UserRepository;
import com.client.emailauth.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
   private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserEntity registerUser(UserModel userModel) {
        UserEntity user = new UserEntity();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, UserEntity user) {
        VerificationToken verificationToken = new VerificationToken(user,token);
        this.verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =  verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
                return "Invalid";
        }

//        Checking Token Duration!
        UserEntity user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "expired!";
        }

//        If user's token is verified and it is not invalid then enable user account!
        user.setEnable(true);
        userRepository.save(user);

        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {
         PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
        /*Here we are using he same entity to reset the password!*/
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken =  passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid";
        }

//        Checking Token Duration!
        UserEntity user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired!";
        }
        return "valid";
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldePassword(UserEntity user, String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }
}
