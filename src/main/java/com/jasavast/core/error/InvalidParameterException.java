package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends BadRequestAlertException{
    public InvalidParameterException(String field){
        super(ErrorConstants.DEFAULT_TYPE,"Invalid parameter "+ field,"verification","400");
    }
}
