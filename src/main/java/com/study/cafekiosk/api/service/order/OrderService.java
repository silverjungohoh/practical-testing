package com.study.cafekiosk.api.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.study.cafekiosk.api.controller.order.request.OrderCreateRequest;
import com.study.cafekiosk.api.service.order.response.OrderResponse;
import com.study.cafekiosk.domain.order.Order;
import com.study.cafekiosk.domain.order.OrderRepository;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredAt) {
		List<String> productNumbers = request.getProductNumbers();
		List<Product> duplicatedProducts = findProductsBy(productNumbers);

		Order order = Order.create(duplicatedProducts, registeredAt);
		Order savedOrder = orderRepository.save(order);
		return OrderResponse.from(savedOrder);
	}

	private List<Product> findProductsBy(List<String> productNumbers) {
		List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

		Map<String, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product :: getProductNumber, p -> p));

		return productNumbers.stream()
			.map(productMap :: get)
			.collect(Collectors.toList());
	}
}
