package com.jasavast.api;

import com.jasavast.BoilerPlateApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {BoilerPlateApplication.class})
@Slf4j
@ActiveProfiles("dev")
public class ApiNasabahTest {

    public void createNasabah(){

    }
}
