package com.dreamcc.error.cuncurrent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: error1 tomcat会重用线程池
 * @Author: dreamcc
 * @Date: 2020/11/10 11:37
 * @Version: V1.0
 */
@RestController
@RequestMapping("/threadLocal")
public class ThreadLocalDemo {

	private static final ThreadLocal<Integer> currentUser = ThreadLocal.withInitial(() -> null);

	@GetMapping("/wrong")
	public Map wrong(@RequestParam("userId") Integer userId) {
		String before = Thread.currentThread().getName() + ":" + currentUser.get();
		currentUser.set(userId);
		String after = Thread.currentThread().getName() + ":" + currentUser.get();
		//汇总输出两次查询结果
		Map result = new HashMap();
		result.put("before", before);
		result.put("after", after);
		return result;
	}

	@GetMapping("/right")
	public Map right(@RequestParam("userId") Integer userId) {
		String before = Thread.currentThread().getName() + ":" + currentUser.get();
		currentUser.set(userId);
		String after = Thread.currentThread().getName() + ":" + currentUser.get();
		//汇总输出两次查询结果
		try {
			Map result = new HashMap();
			result.put("before", before);
			result.put("after", after);
			return result;
		} finally {
			currentUser.remove();
		}
	}
	@GetMapping("/hello")
	public String hello(){
		return "Hello World1";
	}
}
