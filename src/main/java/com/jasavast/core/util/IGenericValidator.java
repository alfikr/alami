package com.jasavast.core.util;

import org.springframework.validation.Errors;

public interface IGenericValidator {
    public void afterPropertiesSet() throws Exception;
    public boolean supports(Class clazz);
    public void validate(Object target, Errors errors);
}
