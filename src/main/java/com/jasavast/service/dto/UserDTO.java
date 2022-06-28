package com.jasavast.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private boolean aktif;
    private LocalDate tanggalLahir;
    private Set<String> authoritySet= new HashSet<>();
}
