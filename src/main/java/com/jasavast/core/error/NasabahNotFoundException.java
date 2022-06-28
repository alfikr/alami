package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NasabahNotFoundException extends BadRequestAlertException{
    public NasabahNotFoundException(){
        super(ErrorConstants.INVALID_ID_TYPE,"Nasabah tidak ditemukan","nasabah","404");
    }
}
