package com.poly.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.poly.admin.model.UserFormModel;
import com.poly.common.entity.User;

public interface UserService {
	public User getUserByEmail(String email);

	public User saveFromUserModelToUser(UserFormModel userModel, Integer id);

	public List<User> findAll();

	public User findById(Integer id);

	public User updateEnabled(Integer id);

	public UserFormModel loadFromUserToUserModel(Integer id);

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword);

	public void delete(Integer id);

	public User checkUserBeforeDelete(Integer id);
}
