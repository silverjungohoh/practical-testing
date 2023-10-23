package com.study.cafekiosk.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.study.cafekiosk.api.service.product.response.ProductResponse;
import com.study.cafekiosk.domain.order.Order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {

	private Long id;

	private int totalPrice;

	private LocalDateTime registeredAt;

	private List<ProductResponse> products;

	public static OrderResponse from(Order order) {
		return OrderResponse.builder()
			.id(order.getId())
			.totalPrice(order.getTotalPrice())
			.registeredAt(order.getRegisteredAt())
			.products(order.getOrderProducts()
				.stream()
				.map(orderProduct -> ProductResponse.from(orderProduct.getProduct()))
				.collect(Collectors.toList()))
			.build();
	}
}
