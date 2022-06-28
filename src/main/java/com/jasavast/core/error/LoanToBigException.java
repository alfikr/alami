package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanToBigException extends BadRequestAlertException{
    public LoanToBigException(double saldo,double pinjaman){
        super(ErrorConstants.MAX_FORGOT_REACHED_TYPE,"Pinjaman "+pinjaman+" terlalu besar, sisa saldo "+saldo,"pinjaman","400");
    }
}
