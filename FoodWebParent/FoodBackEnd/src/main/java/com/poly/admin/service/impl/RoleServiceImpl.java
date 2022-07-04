package com.poly.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.admin.repository.RoleRepository;
import com.poly.admin.service.RoleService;
import com.poly.common.entity.Role;

@Service
public class RoleServiceImpl implements RoleService{
	@Autowired
	RoleRepository roleRepo;
	
	@Override
	public List<Role> findAll() {
		return roleRepo.findAll();
	}
	
}
