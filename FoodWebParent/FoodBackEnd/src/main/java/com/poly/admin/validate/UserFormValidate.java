package com.poly.admin.validate;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.poly.admin.model.UserFormModel;
import com.poly.admin.service.UserService;
import com.poly.common.entity.User;

@Component
public class UserFormValidate implements Validator{
	@Autowired
	private UserService userService;
	
	// Bien dung de bat loi dinh dang email
	private EmailValidator emailValidator = EmailValidator.getInstance();
	
	
	@Override
	public boolean supports(Class<?> clazz) {
		// Rang buoc form voi model tuong ung de bat loi
		return clazz == UserFormModel.class;
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserFormModel userForm = (UserFormModel) target;
		
		// Bat loi truong email bo trong hoac null
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotBlank.userForm.email");
		
		// Bat loi truong firstName bo trong hoac null
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotBlank.userForm.firstName");
		
		// Bat loi truong lastName bo trong hoac null
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotBlank.userForm.lastName");
		
		// Bat loi truong enabled khong chon
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enabled", "NotBlank.userForm.enabled");
		
		if (userForm.getId() == null) {
			// Bat loi truong password bo trong hoac null
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.userForm.password");
		}	
		
		// Truong hop email da nhap gi do
		if (!errors.hasFieldErrors("email")) {
			// Bat loi sai dinh dang email
			if (!emailValidator.isValid(userForm.getEmail())) {
				errors.rejectValue("email", "Pattern.userForm.email");
			}
			// Bat loi email bi nhap qua 128 ky tu
			else if (userForm.getEmail().length() > 128) {
				errors.rejectValue("email", "Max.userForm.email");
			} 
			// Truong hop 2 dieu kien kia khong xay ra
			else {
				// Neu nhu tao moi mot user thi moi bat loi email cua user co ton tai hay chua
				if (userForm.getId() == null) {
					// Kiem tra user co ton tai trong dtb hay chua
					User user = userService.getUserByEmail(userForm.getEmail());
					// Bat loi trung email neu nhu email da ton tai
					if (user != null) {
						errors.rejectValue("email", "Duplicate.userForm.email");
					}
				}		
			}
		}
		
		// Truong hop firstName da nhap gi do
		if (!errors.hasFieldErrors("firstName")) {
			// Bat loi firstName bi nhap qua 45 ky tu
			if (userForm.getFirstName().length() > 45) {
				errors.rejectValue("firstName", "Max.userForm.firstName");
			}
		}
		
		// Truong hop lastName da nhap gi do
		if (!errors.hasFieldErrors("lastName")) {
			// Bat loi firstName bi nhap qua 45 ky tu
			if (userForm.getLastName().length() > 45) {
				errors.rejectValue("lastName", "Max.userForm.lastName");
			}
		}
		
		// Truong hop password da nhap gi do
		if (userForm.getPassword().length() != 0) {
			int lengthPassword = userForm.getPassword().length();
			// Bat loi password bi nhap qua 25 ky tu va nho hon 8 ky tu
			if ((lengthPassword > 25) || (lengthPassword < 8)) {
				errors.rejectValue("password", "Max.userForm.password");
			}
		}
		
	}
	
}
