package com.poly.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.poly.admin.repository.UserRepository;
import com.poly.common.Constants;
import com.poly.common.entity.Role;
import com.poly.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userKhoaPH = new User("khoahoangyahoo@gmail.com", "1234567", "Khoa", "Phạm");
		userKhoaPH.addRoles(roleAdmin);
		
		User userAdded = repo.save(userKhoaPH);
		assertThat(userAdded.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testPagination() {
		int pageNum = 2;
		Pageable pageable = PageRequest.of(pageNum, 5);
		Page<User> pageUser = repo.findAll(pageable);
		List<User> listUsers = pageUser.getContent();
		
		System.out.println(pageUser.getNumber());
		System.out.println(pageUser.getTotalElements());
		
		int startCount = 1 + ((pageNum - 1) * Constants.NUMBER_USER_PER_PAGE);
	}
	
	@Test
	public void testDeleteUser() {
		User user = repo.findById(11).get();
		
		repo.delete(user);
		
		//assertThat(user.getId()).isGreaterThan(0);
	}
}
