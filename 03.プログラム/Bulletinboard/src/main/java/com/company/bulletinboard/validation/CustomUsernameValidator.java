package com.company.bulletinboard.validation;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class CustomUsernameValidator extends FieldValidatorSupport {
	@Override
	public void validate(Object object) throws ValidationException {
		String fieldName = getFieldName();
		String value = (String) getFieldValue(fieldName, object);
		if (!validateUsername(value)) {
			addFieldError(fieldName, object);
		}
	}

	public static boolean validateUsername(String username) {
		return username != null && username.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
	}
}
