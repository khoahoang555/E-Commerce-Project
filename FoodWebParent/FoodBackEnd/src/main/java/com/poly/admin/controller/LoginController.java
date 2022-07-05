package com.poly.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.admin.model.LoginFormModel;
import com.poly.admin.validate.LoginFormValidate;

@Controller
@RequestMapping("/admin")
public class LoginController {
	
	@Autowired
	private LoginFormValidate loginFormValidate;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		Object target = binder.getTarget();
		if (target == null) {
			return;
		}
		if (target.getClass() == LoginFormModel.class) {
			binder.setValidator(loginFormValidate);
		}
	}
	
	@GetMapping("/login")
	public String viewLoginPage(Model model) {
		LoginFormModel loginForm = new LoginFormModel();
		model.addAttribute("loginForm", loginForm);
		return "security/login";
	}
	
	@PostMapping("/login")
	public String handlerLogin(Model model, @ModelAttribute("loginForm") @Validated LoginFormModel loginForm, 
			BindingResult result) {
		if (result.hasErrors()) {
			return "security/login";
		}
		
		return "dashboard/list_dashboard";
	}
}
