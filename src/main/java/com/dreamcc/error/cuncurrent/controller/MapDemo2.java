package com.dreamcc.error.cuncurrent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Title: error
 * @Author: dreamcc
 * @Date: 2020/11/10 14:32
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/mapDemo2")
public class MapDemo2 {

	private static int LOOP_COUNT = 10000000;

	private static int THREAD_COUNT = 10;

	//元素数量
	private static int ITEM_COUNT = 10;


	private Map<String,Long> normaluse() throws InterruptedException{
		ConcurrentHashMap<String,Long> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
		ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
		forkJoinPool.execute(
				()-> IntStream.rangeClosed(1,LOOP_COUNT).parallel().forEach(
						i->{
							String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
							synchronized (freqs) {
								if(freqs.containsKey(key)){
									freqs.put(key,freqs.get(key)+1);
								}else {
									freqs.put(key,1L);
								}
							}
						}
				)

		);
		forkJoinPool.shutdown();
		forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
		return freqs;
	}


	private Map<String,Long> gooduse() throws InterruptedException{
		ConcurrentHashMap<String,LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
		ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
		forkJoinPool.execute(
				()-> IntStream.rangeClosed(1,LOOP_COUNT).parallel().forEach(
						i->{
							String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
							freqs.computeIfAbsent(key,k -> new LongAdder()).increment();
						}
				)

		);
		forkJoinPool.shutdown();
		forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
		return freqs.entrySet().stream()
				.collect(Collectors.toMap(
						e->e.getKey(),
						e->e.getValue().longValue()
				));
	}

	@RequestMapping("/good")
	public String good() throws InterruptedException{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("nomaluse");
		Map<String,Long> normaluse = normaluse();
		stopWatch.stop();

		Assert.isTrue(normaluse.size() == ITEM_COUNT,"normaluse size error");

		Assert.isTrue(normaluse.entrySet().stream().mapToLong(item -> item.getValue())
				.reduce(0,Long::sum) == LOOP_COUNT,"normaluse count error");

		stopWatch.start("gooduse");
		Map<String,Long> gooduse = gooduse(); stopWatch.stop();

		Assert.isTrue(gooduse.size() == ITEM_COUNT, "gooduse size error");

		Assert.isTrue(gooduse.entrySet().stream() .mapToLong(item -> item.getValue())
				.reduce(0, Long::sum) == LOOP_COUNT , "gooduse count error");

		log.info(stopWatch.prettyPrint());


		return "OK";
	}
}
