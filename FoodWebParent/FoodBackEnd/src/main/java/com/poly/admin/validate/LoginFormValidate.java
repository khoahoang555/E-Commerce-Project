package com.poly.admin.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.poly.admin.model.LoginFormModel;
import com.poly.admin.service.UserService;
import com.poly.common.entity.User;

@Component
public class LoginFormValidate implements Validator{
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == LoginFormModel.class;
	}

	@Override
	public void validate(Object target, Errors errors) {
		LoginFormModel loginForm = (LoginFormModel) target;
		
		// Kiem tra username co rong hay bi bo trong hay khong
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotBlank.loginForm.username");
		
		// Kiem tra password co rong hay bi bo trong hay khong
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.loginForm.password");
		
		// Neu nhu username co nhap thong tin thi kiem tra username co ton tai trong database hay khong
		if (!errors.hasFieldErrors("username")) {
			User user = userService.getUserByEmail(loginForm.getUsername());
			// Neu user khong ton tai trong database thi xuat loi
			if (user == null) {
				errors.rejectValue("username", "NotExist.loginForm.username");
			} 
			// Neu user ton tai trong database thi kiem tra mat khau co khop voi username hay khong
			else {
				// Neu mat khau khong khop voi mat khau trong database thi xuat loi
				if ((!errors.hasFieldErrors("password")) && (!passwordEncoder.matches(loginForm.getPassword(), user.getPassword()))) {				
					errors.rejectValue("password", "NotMatch.loginForm.password");				
				}				
			}
		}
	}

}
