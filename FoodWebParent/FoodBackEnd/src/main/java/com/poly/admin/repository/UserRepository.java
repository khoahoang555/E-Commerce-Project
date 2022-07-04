package com.poly.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.poly.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>{
	@Query("SELECT u FROM User u WHERE u.email = ?1")
	public User getUserByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE CONCAT (u.id, ' ', u.email, ' ', "
			+ "u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);

}
