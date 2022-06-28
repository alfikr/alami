package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserForgotReachLimitException extends BadRequestAlertException{
    public UserForgotReachLimitException(String s){
        super(ErrorConstants.MAX_FORGOT_REACHED_TYPE,s,"user","400");
    }
}
