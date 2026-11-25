package com.SmartPackageSurveillance.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
public class LoginRequest {

	public LoginRequest() {
	}

	@NotBlank
	@Email
	private String email ;
	@NotBlank
	@Size(min=8 , max=100)
	private String password;
	public LoginRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8, max = 100) String password) {
		super();
		this.email = email;
		this.password = password;
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
	
}
