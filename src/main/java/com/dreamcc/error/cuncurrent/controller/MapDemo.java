package com.dreamcc.error.cuncurrent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @Title: error
 * @Author: dreamcc
 * @Date: 2020/11/10 14:02
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/mapDemo")
public class MapDemo {
	//线程个数
	private static int THREAD_COUNT = 10;
	//总元素数
	private static int ITEM_COUNT = 1000;

	//帮助方法，用来获得一个指定元素数量模拟数据的map
	public ConcurrentHashMap<String,Long> getData(int count){
		return LongStream.rangeClosed(1,count)
				.boxed()
				.collect(
						Collectors.toConcurrentMap(i-> UUID.randomUUID().toString(),
								Function.identity(),
								(o1,o2)->o1,ConcurrentHashMap::new));
	}
	@GetMapping("/wrong")
	public String wrong() throws InterruptedException{
		ConcurrentHashMap<String,Long> concurrentHashMap = getData(ITEM_COUNT - 100);
		//初始900个元素
		log.info("init size:{}", concurrentHashMap.size());

		ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
		forkJoinPool.execute(
				()-> IntStream.rangeClosed(1,10).parallel().forEach(
					i->{
						int gap = ITEM_COUNT - concurrentHashMap.size();
						log.info("gap size:{}", gap);
						concurrentHashMap.putAll(getData(gap));
					}
				)
		);
		forkJoinPool.shutdown();
		forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
		log.info("finish size:{}", concurrentHashMap.size());
		return "OK";
	}

	@GetMapping("/right")
	public String right() throws InterruptedException{
		ConcurrentHashMap<String,Long> concurrentHashMap = getData(ITEM_COUNT - 100);
		//初始900个元素
		log.info("init size:{}", concurrentHashMap.size());

		ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
		forkJoinPool.execute(
				()-> IntStream.rangeClosed(1,10).parallel().forEach(
						i->{
							synchronized (concurrentHashMap) {
								int gap = ITEM_COUNT - concurrentHashMap.size();
								log.info("gap size:{}", gap);
								concurrentHashMap.putAll(getData(gap));
							}
						}
				)
		);
		forkJoinPool.shutdown();
		forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
		log.info("finish size:{}", concurrentHashMap.size());
		return "OK";
	}
}
