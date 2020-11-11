package com.dreamcc.error.java8user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: error
 * @Author: dreamcc
 * @Date: 2020/11/11 10:52
 * @Version: V1.0
 */
public class Demo1 {

	//订单类
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class OrderEntity {
		private Long id;
		private Long customerId;//顾客ID
		private String customerName;//顾客姓名
		private List<OrderItem> orderItemList;//订单商品明细
		private Double totalPrice;//总价格
		private LocalDateTime placedAt;//下单时间
	}
	//订单商品类
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class OrderItem {
		private Long productId;//商品ID
		private String productName;//商品名称
		private Double productPrice;//商品价格
		private Integer productQuantity;//商品数量
	}
	//顾客类
	@Data
	@AllArgsConstructor
	public class Customer {
		private Long id;
		private String name;//顾客姓名
	}
}
