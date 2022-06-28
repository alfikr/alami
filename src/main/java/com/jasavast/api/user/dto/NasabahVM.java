package com.jasavast.api.user.dto;

import com.jasavast.core.error.FieldErrorVM;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
public class NasabahVM {
    @NotEmpty(message = "firstName tidak boleh kosong")
    @Min(value = 3,message = "nama tidak boleh kurang dari 3")
    private String firstName;
    @NotNull(message = "parameter lastName harus ada")
    private String lastName;
    @NotNull(message = "tanggalLahir tidak boleh kosong")
    private LocalDate tanggalLahir;
    @NotEmpty(message = "alamat tidak boleh kosong")
    private String alamat;
}
