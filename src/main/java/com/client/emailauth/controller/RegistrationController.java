package com.client.emailauth.controller;

import com.client.emailauth.entity.UserEntity;
import com.client.emailauth.entity.VerificationToken;
import com.client.emailauth.event.RegistrationCompleteEvent;
import com.client.emailauth.model.PasswordModel;
import com.client.emailauth.model.UserModel;
import com.client.emailauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;
//        Creating Seperate event to generate the emmail for verification of account!
   @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

   @GetMapping("/hello")
   String hello(){
       return "Welcome to Spring Security!";
   }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest httpServletRequest){
    UserEntity user = userService.registerUser(userModel);

//        passing the puclish event that we want to publish, so passing the url by getting from HTTPServelet
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl( httpServletRequest)));
//After Creating the event, now listening the event

        return "Successed to register!";
    }
    private String applicationUrl(HttpServletRequest httpServletRequest) {
        return "http://"
                + httpServletRequest.getServerName()
                + ":" + httpServletRequest.getServerPort()
                + httpServletRequest.getContextPath();
    }

    @GetMapping("/verifyRegistration")
    //After Creating Verfication URl, now creating api for verifavtion!
    public  String verfyRegistration(@RequestParam("token") String token){
            String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verified Successfully";
        }
        else
        {
            return "Bad User!";
        }
    }

    /*If email was not recieved then resend the email with generated token!
    * Taking oldToen and then generating the url again*/
    @GetMapping("/resendVerifyToken")
    public String resendVerifactionToken(@RequestParam("token") String oldToken,HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        UserEntity user = verificationToken.getUser();
//        now at this point we need to send the email to user such as thjis is new verifcation token
        resendVerifactionTokenMail(user,applicationUrl(request),verificationToken);
        return "Verifcation Link sent!";
    }
    private void resendVerifactionTokenMail(UserEntity user, String applicationUrl, VerificationToken verificationToken) {

        String url = applicationUrl
                + "/verifyRegistration?token=" + verificationToken.getToken();
//Here we can send the email but now pasting here
        log.info("Click link to verify your account: {}", url);
    }

    /*API for  resseting the password!
    * User will be passing the mail link to reset the password
    * and then this email will be validate.
    * */
    @PostMapping("/resetPassword")
    public  String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){
        /*First creating Password Model
        * */
        UserEntity user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";

        if (user != null) {
            String token = UUID.randomUUID().toString(); // creating token
            userService.createPasswordResetTokenForUser(user,token);//Setting the password token
            url = passwordResetTokenMail(user,applicationUrl(request),token);
        }
        return url;
    }

    private String passwordResetTokenMail(UserEntity user, String applicationUrl, String token) {
        String url = applicationUrl
                + "/savePassword?token=" + token ;
//Here we can send the email but now pasting here
        log.info("Click link to reset your password: {}", url);
        return url;
    }
//    Creating API for saving the password
    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,@RequestBody PasswordModel passwordModel){
//       First verifying the token
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid") ) {
            return "Invalid Token";
        }
//        Creating Optional User, so whateve the token is, based on that, getting user back
        Optional<UserEntity> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "Passsword Reset Successfully";

        }else
        {
            return "Invalid Token";
        }
    }


//    Creating ChangePassword API, here on the based of email, we change the password

    @PostMapping("/changePassword")
    public  String changePassword(@RequestBody PasswordModel passwordModel){
        UserEntity user = userService.findUserByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldePassword(user,passwordModel.getOldPassword())) {
            return "Invalid old Password!";
        }
         userService.changePassword(user,passwordModel.getNewPassword());

//        If that is all ok, then save the new password
        return "Password changed Successfully!";
    }
}
