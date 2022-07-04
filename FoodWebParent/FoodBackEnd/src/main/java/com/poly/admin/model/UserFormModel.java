package com.poly.admin.model;

import java.util.ArrayList;
import java.util.List;

import com.poly.common.entity.Role;

public class UserFormModel {
	private Integer id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private Integer enabled;
	private String photo;

	private List<Role> roles = new ArrayList<>();

	public UserFormModel() {
	}

	public UserFormModel(String email, String password, String firstName, String lastName, String confirm,
			Integer enabled, String photo) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.photo = photo;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public String getPathImages() {
		if (this.getPhoto() == null) {
			return "/assets/img/image-thumbnail.png";
		} else {
			return "/user-photos/" + this.getId() + "/" + this.getPhoto();
		}
	}
}
