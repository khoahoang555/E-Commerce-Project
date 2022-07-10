package com.poly.admin.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poly.admin.model.UserFormModel;
import com.poly.admin.repository.UserRepository;
import com.poly.admin.service.UserService;
import com.poly.common.Constants;
import com.poly.common.entity.Role;
import com.poly.common.entity.User;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public User getUserByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	@Override
	public User saveFromUserModelToUser(UserFormModel userModel, Integer id) {
		User user = null;
		List<Role> listRoles = userModel.getRoles();
		boolean enabled = userModel.getEnabled()==1?true:false;
		
		if (id == null) {
			String password = encoder.encode(userModel.getPassword());
			user = new User();		
			user.setPassword(password);
			user.setPhotos(userModel.getPhoto());
		} else {		
			user = userRepo.findById(id).get();			
			if (!userModel.getPassword().isEmpty()) {
				String password = encoder.encode(userModel.getPassword());
				user.setPassword(password);
			} 
			if (userModel.getPhoto() != null) {
				user.setPhotos(userModel.getPhoto());
			}
		}
		user.setEmail(userModel.getEmail());
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setEnabled(enabled);
		
		Set<Role> userRole = new HashSet<>(listRoles);
		
		user.setRoles(userRole);
		
		User userSaved = userRepo.save(user);
		
		return userSaved;
	}

	@Override
	public List<User> findAll() {
		return (List<User>) userRepo.findAll();
	}

	@Override
	public User findById(Integer id) {
		return userRepo.findById(id).get();
	}

	@Override
	public User updateEnabled(Integer id) {
		User user = userRepo.findById(id).get();
		boolean enabled = !user.getEnabled();
		
		user.setEnabled(enabled);
		return userRepo.save(user);
	}

	@Override
	public UserFormModel loadFromUserToUserModel(Integer id) {
		User user = userRepo.findById(id).get();
		UserFormModel userModel = new UserFormModel();
		
		if (user != null) {
			userModel.setEmail(user.getEmail());
			userModel.setEnabled(user.getEnabled()?1:0);
			userModel.setFirstName(user.getFirstName());
			userModel.setLastName(user.getLastName());
			userModel.setId(user.getId());
			userModel.setPhoto(user.getPhotos());
			userModel.setRoles(List.copyOf(user.getRoles()));
		}
				
		return userModel;
	}

	@Override
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc")?sort.ascending():sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, Constants.NUMBER_USER_PER_PAGE, sort);
		
		return (keyword == null) ? userRepo.findAll(pageable) : userRepo.findAll(keyword, pageable);
	}

	@Override
	public void delete(Integer id) {
		User user = userRepo.findById(id).get();
		userRepo.delete(user);
	}

	@Override
	public User checkUserBeforeDelete(Integer id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		
		User user = userRepo.findById(id).get();
		if (user.getEmail().equals(username)) {
			return null;
		}
		
		return user;
	}

}
