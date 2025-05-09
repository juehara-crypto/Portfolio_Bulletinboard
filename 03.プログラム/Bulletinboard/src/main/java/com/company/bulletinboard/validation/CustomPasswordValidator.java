package com.company.bulletinboard.validation;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class CustomPasswordValidator extends FieldValidatorSupport {
	@Override
	public void validate(Object object) throws ValidationException {
		String fieldName = getFieldName();
		String value = (String) getFieldValue(fieldName, object);
		if (!validatePassword(value)) {
			addFieldError(fieldName, object);
		}
	}

	public static boolean validatePassword(String password) {
		return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).*$");
	}
}
