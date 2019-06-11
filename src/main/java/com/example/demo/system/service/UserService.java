package com.example.demo.system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.system.dto.User;

public interface UserService {
	User getUser(String username);
	List<User> getAll();
}
