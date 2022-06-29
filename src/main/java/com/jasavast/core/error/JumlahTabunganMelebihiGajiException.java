package com.jasavast.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JumlahTabunganMelebihiGajiException extends BadRequestAlertException{
    public JumlahTabunganMelebihiGajiException(){
        super(ErrorConstants.SALDO_NOT_ENOUGH_TYPE,"jumlah tabungan tidak boleh melebihi gaji","tabungan","400");
    }
}
