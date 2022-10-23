package com.client.emailauth.event;

import com.client.emailauth.entity.UserEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

  private   UserEntity user;
    private  String applicationUrl;

    //    Now event has created
    public RegistrationCompleteEvent(UserEntity user,String applicationUrl) {
        super(user);

        this.user = user;
        this.applicationUrl = applicationUrl;
    }


}
