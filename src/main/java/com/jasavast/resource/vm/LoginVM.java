package com.jasavast.resource.vm;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginVM {
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;
    @NotNull
    private boolean rememberMe;

}
