package com.poly.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.poly.admin.service.UserService;
import com.poly.common.entity.User;

public class FoodingUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.getUserByEmail(username);
		if (user != null) {
			System.out.println("User login have email: " + username);
			return new FoodingUserDetails(user);
		}
		throw new UsernameNotFoundException("Could not find user with email: " + username);
	}

}
