package com.client.emailauth.event.listener;

import com.client.emailauth.entity.UserEntity;
import com.client.emailauth.event.RegistrationCompleteEvent;
import com.client.emailauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
//using the ApplicationListener Generic interface and passing the event that is created!
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
//Now Creating the verification token for the user, Whatever the token is created, and will attach the link,
// so when user will click the url, will  be redirected  back to the apps with link.
        UserEntity user = event.getUser();
        String token  = UUID.randomUUID().toString(); // creating token for user, and savefor particular user,and match this token with DB
        userService.saveVerificationTokenForUser(token,user);
//       Once created link here, send email to user, that's the two things which need to perform

        String url = event.getApplicationUrl()
                    + "/verifyRegistration?token=" + token;
                ;
//Here we can send the email but now pasting here
          log.info("Click link to verify your account: {}", url);

    }
}
