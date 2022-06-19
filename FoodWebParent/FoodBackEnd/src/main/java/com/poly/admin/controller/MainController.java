package com.poly.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("")
	public String viewHomePage() {
		return "dashboard/list_dashboard";
	}
}
