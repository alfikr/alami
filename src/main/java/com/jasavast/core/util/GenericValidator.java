package com.jasavast.core.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Set;

@Component
@Slf4j
public class GenericValidator implements org.springframework.validation.Validator, InitializingBean,IGenericValidator, SmartValidator {
    private Validator validator;

    public void afterPropertiesSet() throws Exception{
        ValidatorFactory factory= Validation.buildDefaultValidatorFactory();
        validator=factory.usingContext().getValidator();
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolationtSet=validator.validate(target);
        for (ConstraintViolation<Object> constraintViolation: constraintViolationtSet){

            String propertyPath= constraintViolation.getPropertyPath().toString();
            String message=constraintViolation.getMessage();
            errors.rejectValue(propertyPath,"",message);
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        validate(target,errors);
    }

    @Override
    public void validateValue(Class<?> targetType, String fieldName, Object value, Errors errors, Object... validationHints) {
        SmartValidator.super.validateValue(targetType, fieldName, value, errors, validationHints);
    }
    public JSONObject validationKey(JSONObject object, String... param){
        JSONObject result = new JSONObject();
        Arrays.stream(param)
                .forEach(s -> {
                    if (!object.has(s)){
                        result.put(s,s+" tidak boleh kosong");
                    }
                });
        log.info("result {}",result);
        return result;
    }
}
