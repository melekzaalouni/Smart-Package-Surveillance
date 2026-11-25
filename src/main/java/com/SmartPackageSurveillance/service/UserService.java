package com.SmartPackageSurveillance.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartPackageSurveillance.dto.UserResponse;
import com.SmartPackageSurveillance.entity.User;
import com.SmartPackageSurveillance.enumaration.UserRole;
import com.SmartPackageSurveillance.exception.UserNotFoundException;
import com.SmartPackageSurveillance.repository.UserRepository;

@Service
public class UserService {
	private UserRepository userRepository ; 
	
	public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	public UserResponse converttoUserResponse(User dto ) {
		//UserResponse user = new UserResponse();
		return new UserResponse(dto.getName(), dto.getEmail(), dto.getRole(), dto.getCreatedAt());
	}
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("user not found "));
		return converttoUserResponse(user);
	}
	public UserResponse getUserByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("user not found "));
		return converttoUserResponse(user);
	}
	public UserResponse getCurrentUserProfile () {
		String email= SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getName();
		 User user = userRepository.findByEmail(email)
		            .orElseThrow(() ->
		                new UserNotFoundException("User not found"));

		    return converttoUserResponse(user);
	}
	

	/** Admin changes a user's role (CUSTOMER <-> ADMIN). Takes effect on the user's next request. */
	@Transactional
	public UserResponse changeRole(String email, String role) {
		if (email == null || role == null) {
			throw new IllegalArgumentException("email and role are required");
		}
		UserRole newRole;
		try {
			newRole = UserRole.valueOf(role.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Role must be ADMIN or CUSTOMER");
		}
		String caller = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		if (user.getEmail().equalsIgnoreCase(caller) && newRole != UserRole.ADMIN) {
			throw new IllegalArgumentException("You cannot remove your own admin role");
		}
		user.setRole(newRole);
		return converttoUserResponse(userRepository.save(user));
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::converttoUserResponse).toList();
	}
}
