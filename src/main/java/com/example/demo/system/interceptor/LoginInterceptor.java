package com.example.demo.system.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.demo.system.dto.User;
import com.example.demo.system.utils.RedisUtil;
import com.example.demo.utils.Results;
 
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println(111111111);
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
        	System.out.println("aaa");
            return true;
        }
        //如果被@LoginRequired注解代表不需要登录验证，直接通过
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(method.getAnnotation(LoginRequired.class) != null) return true;       
        //token验证
        boolean l = false;
        String token = request.getHeader("token");
        System.out.println(token+"-------");
        

        if(StringUtils.isNotEmpty(token)) {
        	 String userid = redisUtil.get(token);
        	 System.out.println(userid+"-------");
        	 if(userid != null) {
        		 String redistoken = redisUtil.get(userid);
        		 System.out.println(redistoken+"++++++++");
        		 //验证通过
        		 if(redistoken.equals(token)) {
        			 System.out.println(2222);
        			 redisUtil.expire(token, 60);
        			 redisUtil.expire(userid, 60);
        			 l = true;
        		 }
        	 
        	 }
        }
        //验证通过
        if(l) {
            request.setAttribute("userid", new User());
        }else {
        
	        //验证未通过
	        response.setCharacterEncoding("UTF-8");
	        response.setContentType("application/json; charset=utf-8");
	        response.getWriter().write("{\"code\":\"401\",\"msg\":\"权限未认证\"}");
        }
		return l; 
    }
}