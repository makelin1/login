
  package com.example.demo.system.dao;
  
  
  import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
  
  import com.example.demo.system.dto.User;
  
  @Repository
  public interface UserDao {
	  //User selByUsername(String username); 
	  User getUser(@Param(value="username") String id);

	  List<User> getAll();
  }
  
 