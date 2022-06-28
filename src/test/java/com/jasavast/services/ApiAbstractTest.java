package com.jasavast.services;


import com.jasavast.BoilerPlateApplication;
import com.jasavast.service.ApiAbstract;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {BoilerPlateApplication.class})
public class ApiAbstractTest {

    public void testApi(){
        ApiAbstract apiAbstract=new ApiAbstract(null);
        JSONObject object=new JSONObject();
        object.put("data",new JSONObject());
        apiAbstract.init(object);
        Assertions.assertNotNull(apiAbstract);
    }
}
