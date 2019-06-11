package com.example.demo.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.system.dao.UserDao;
import com.example.demo.system.dto.User;
import com.example.demo.system.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Override
	public User getUser(String username) {
		// TODO Auto-generated method stub
		User user = userDao.getUser(username);
		return user;
	}

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		List<User> list = userDao.getAll();
		return list;
	}
	
	
}
