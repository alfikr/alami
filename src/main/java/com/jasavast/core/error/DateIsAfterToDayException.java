package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DateIsAfterToDayException extends BadRequestAlertException{
    public DateIsAfterToDayException(){
        super(ErrorConstants.DATE_AFTER_TODAY_TYPE,"Tanggal tidak boleh lebih dari hari ini","transaksi","400");
    }
}
