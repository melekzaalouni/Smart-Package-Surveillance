package com.SmartPackageSurveillance.entity;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

import com.SmartPackageSurveillance.enumaration.UserRole;

import jakarta.persistence.*;


@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long id  ; 
	@Column(name="name" , nullable = false )
	private String name ; 
	@Column(name="email" , nullable = false , unique = true )
	private String email ; 
	@Column(name="password", nullable = false)
	private String password ; 
	@Enumerated(EnumType.STRING)
	@Column(name="role" , nullable = false )
	private UserRole role ; 
	@Column(name="createdAt" , nullable =false)
	private LocalDateTime createdAt ;
	
	public User() {
		super();
	}
	public User(Long id, String name, String email, String password, UserRole role, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.createdAt = createdAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
