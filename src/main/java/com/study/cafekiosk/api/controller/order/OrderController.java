package com.study.cafekiosk.api.controller.order;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.cafekiosk.api.controller.order.request.OrderCreateRequest;
import com.study.cafekiosk.api.service.order.OrderService;
import com.study.cafekiosk.api.service.order.response.OrderResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/orders/new")
	public OrderResponse createOrder(@RequestBody OrderCreateRequest request) {
		return orderService.createOrder(request, LocalDateTime.now());
	}
}
