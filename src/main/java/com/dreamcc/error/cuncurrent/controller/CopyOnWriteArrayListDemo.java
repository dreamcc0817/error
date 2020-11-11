package com.dreamcc.error.cuncurrent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Title: error
 * @Author: dreamcc
 * @Date: 2020/11/10 15:16
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/copyOnWriteArrayListDemo")
public class CopyOnWriteArrayListDemo {

	//测试并发写的性能
	@GetMapping("/write")
	public Map testWrite() {
		List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
		List<Integer> synchronizeArrayList = Collections.synchronizedList(new ArrayList<>());
		StopWatch stopWatch = new StopWatch();
		int loopCount = 100000;
		stopWatch.start("Write:copyOnWriteArrayList");

		IntStream.rangeClosed(1, loopCount).parallel().forEach(
				x -> copyOnWriteArrayList.add(ThreadLocalRandom.current().nextInt(loopCount)));
		stopWatch.stop();

		stopWatch.start("Write:synchronizedList");

		IntStream.rangeClosed(1, loopCount).parallel().forEach(
				x -> synchronizeArrayList.add(ThreadLocalRandom.current().nextInt(loopCount)));
		stopWatch.stop();

		log.info(stopWatch.prettyPrint());

		Map result = new HashMap();
		result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
		result.put("synchronizedList", synchronizeArrayList.size());

		return result;
	}

	private void addAll(List list) {
		list.addAll(IntStream.rangeClosed(1, 1000000).boxed().collect(Collectors.toList()));
	}

	@GetMapping("/read")
	public Map testRead() {
		List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
		List<Integer> synchronizeArrayList = Collections.synchronizedList(new ArrayList<>());

		addAll(copyOnWriteArrayList);
		addAll(synchronizeArrayList);

		StopWatch stopWatch = new StopWatch();


		int loopCount = 1000000;
		int count = copyOnWriteArrayList.size();

		stopWatch.start("Read:copyOnWriteArrayList");
		//循环1000000次并发从CopyOnWriteArrayList随机查询元素
		IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> copyOnWriteArrayList.get(ThreadLocalRandom.current().nextInt(count)));
		stopWatch.stop();
		stopWatch.start("Read:synchronizedList");
		//循环1000000次并发从加锁的ArrayList随机查询元素
		IntStream.range(0, loopCount).parallel().forEach(__ -> synchronizeArrayList
				.get(ThreadLocalRandom.current().nextInt(count)));
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		Map result = new HashMap();
		result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
		result.put("synchronizedList", synchronizeArrayList.size());
		return result;
	}
}
