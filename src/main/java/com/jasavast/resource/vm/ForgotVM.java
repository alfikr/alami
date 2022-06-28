package com.jasavast.resource.vm;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class ForgotVM {
    @NotNull
    private String email;
}
