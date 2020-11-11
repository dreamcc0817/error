package com.dreamcc.error;

import com.dreamcc.error.java8user.Demo1;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

/**
 * @Title: error
 * @Author: dreamcc
 * @Date: 2020/11/10 18:00
 * @Version: V1.0
 */

@SpringBootTest
public class TestDemo {

	List<Demo1.OrderEntity> orders = Arrays.asList();

	@Test
	public void streamDemo() {
		Arrays.asList("a1", "a2", "a3").stream().forEach(System.out::println);
		Arrays.stream(new int[]{1, 2, 3}).forEach(System.out::println);
	}

	@Test
	public void of() {
		String[] arr = {"a", "b", "c"};
		Stream.of(arr).forEach(System.out::println);
		Stream.of("a", "b", "b").forEach(System.out::println);
		Stream.of(1, 2, "a").map(item -> item.getClass().getName()).forEach(System.out::println);
	}

	@Test
	public void iterate() {
		Stream.iterate(2, integer -> integer * 2).limit(10).forEach(System.out::println);
		Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.TEN)).limit(10).forEach(System.out::println);
	}

	@Test
	public void generate() {
		Stream.generate(() -> "test").limit(3).forEach(System.out::println);
		Stream.generate(Math::random).limit(10).forEach(System.out::println);
	}

	@Test
	public void order() {

		orders.stream().filter(Objects::isNull)
				.filter(orderEntity -> orderEntity.getPlacedAt().isAfter(LocalDateTime.now().minusDays(6)))
				.forEach(System.out::println);


		LongAdder longAdder = new LongAdder();
		orders.stream()
				.forEach(orderEntity -> orderEntity
						.getOrderItemList()
						.forEach(orderItem -> longAdder.add(orderItem.getProductQuantity())));


		long sums = orders.stream()
				.mapToLong(orderEntity -> orderEntity
						.getOrderItemList()
						.stream()
						.mapToLong(Demo1.OrderItem::getProductQuantity)
						.sum())
				.sum();

		Assert.assertEquals(longAdder.longValue(), sums);

	}

	@Test
	public void demo2() {
		Random random = ThreadLocalRandom.current();
		System.out.println(random.ints(48, 122)
				.filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
				.mapToObj(i -> (char) i)
				.limit(20)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString());
	}


	public void demo3(){

	}
}
