package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaldoNotEnoughException extends BadRequestAlertException{
    public SaldoNotEnoughException(){
        super(ErrorConstants.SALDO_NOT_ENOUGH_TYPE,"Saldo tidak mencukupi","nasabah","400");
    }
}
