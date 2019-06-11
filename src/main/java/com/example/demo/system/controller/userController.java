package com.example.demo.system.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;



import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.base.BaseController;
//import com.example.demo.system.dao.UserDao;
import com.example.demo.system.dto.User;
import com.example.demo.system.interceptor.LoginRequired;
import com.example.demo.system.service.UserService;
import com.example.demo.system.utils.RedisUtil;
import com.example.demo.utils.Results;

import cn.com.WebXml.WeatherWSLocator;
import cn.com.WebXml.WeatherWSSoapStub;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/*@RunWith(SpringRunner.class)
@SpringBootTest*/

@Controller
@RequestMapping(value = "/users")
@Api(value = "用户的增删改查")
public class userController extends BaseController{
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private UserService userService;
	
	private static final char[] HEX_CODE = "0123456789abcdef".toCharArray();
	/**
     * 登录
     * api :localhost:8080/users
     * @return
	 * @throws NoSuchAlgorithmException 
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    @ApiOperation(value = "登录")
    public Map<String,Object> login(@RequestBody User user,HttpServletRequest requset) throws NoSuchAlgorithmException {
    	System.out.println(user.getUsername()+"ppppppppp");
    	User user2 = userService.getUser(user.getUsername());
    	// 用户不存在
		if (user2 == null) {
			System.out.println("有用");
			return new HashMap();
		}	
		System.out.println("1111");
		// 密码错误
		if (!user.getPassword().equals(user2.getPassword())) {
			System.out.println("密码错误");
			return new HashMap();
		}
			
		String  userid = user2.getUserid();
		System.out.println(userid);
		//判断用户密码 --通过
    	String token = createToken();
    	
    	redisUtil.set(userid,token,60);
    	redisUtil.set(token,userid,60);

    	
    	System.out.println(redisUtil.get(userid));
    	System.out.println(redisUtil.get(token));
    	
    	/** 从header中获取token */
//		String token = requset.getHeader("token");
//		System.out.println(token+"1111");
//		/** 如果header中不存在token，则从参数中获取token */
//		if (StringUtils.isBlank(token)) {
//			
//			token = requset.getParameter("token");
//			
//			System.out.println(token+"22222");
//		}
    	Map<String, Object> m = new HashMap<>();
    	
    	m.put("userid", user2.getUserid());
    	m.put("username", user2.getUsername());
    	m.put("token", token);
         
        return m;
    }
    
    @RequestMapping(value = "/quit",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "登出")
    public Object logout(HttpServletRequest request) {
        String token1 = (String) request.getAttribute("token");
        System.out.println("++++++++++"+token1);
        String token = request.getHeader("token");
        
        if(token != null) {
        	String userid = redisUtil.get(token);
        	redisUtil.remove(token);
        	
        	System.out.println("--------"+userid);
        	if(userid != null) {
            	redisUtil.remove(userid);
            }
        }
        
        return Results.success("200","登出成功");
    }
    
    @RequestMapping(value = "/getUser",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    @ApiOperation(value = "获取用户")
    public List<User> getUser(HttpServletRequest requset) throws NoSuchAlgorithmException {
    	//System.out.println("aaaaaaa");
    	List<User> userList = userService.getAll();
    	System.out.println(userList);
    	/** 从header中获取token */
		String token = requset.getHeader("token");
		System.out.println(token+"1111");
		/** 如果header中不存在token，则从参数中获取token */
		if (StringUtils.isBlank(token)) {
			
			token = requset.getParameter("token");
			
			System.out.println(token+"22222");
		}
    	 
        return userList;
        
    }
    
    @RequestMapping(value = "/getWeather",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    @ApiOperation(value = "获取天气")
    
    public Map<String,Object> getWeather(HttpServletRequest requset) throws NoSuchAlgorithmException, Exception {
    	
    	WeatherWSLocator locator = new WeatherWSLocator();
        WeatherWSSoapStub stub = (WeatherWSSoapStub) locator.getPort(WeatherWSSoapStub.class);
    	 
        String[] weatherInfo = stub.getWeather("石家庄", null);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("weather", weatherInfo);
        System.out.println(map);
        return map;
        
    }
    
    public String createToken() {
		
		/**生成一个token*/
		String token = getToken();
        
        /**缓存用户信息*/
		//暂时不歇
		//System.out.println(token);
		return token;
	}
    
    public static String getToken() {
    	String token = "";
        try {
        	MessageDigest algorithm = MessageDigest.getInstance("MD5");
        	algorithm.reset();
        	String uuid = UUID.randomUUID().toString();
            algorithm.update(uuid.getBytes());
            byte[] messageDigest = algorithm.digest();
            token = toHexString(messageDigest);
            
            
        } catch (Exception e) {
			// TODO: handle exception
		}
        return token;
    }
    
    public static String toHexString(byte[] data) {
        if(data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }

}
