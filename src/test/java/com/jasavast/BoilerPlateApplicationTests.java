package com.jasavast;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = BoilerPlateApplication.class)
@ActiveProfiles("dev")
class BoilerPlateApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generatePassword(){
        System.out.println(passwordEncoder.encode("user"));
    }
}
