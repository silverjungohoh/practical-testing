package com.study.cafekiosk.api.service.order;

import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.study.cafekiosk.api.controller.order.request.OrderCreateRequest;
import com.study.cafekiosk.api.service.order.response.OrderResponse;
import com.study.cafekiosk.domain.order.OrderRepository;
import com.study.cafekiosk.domain.orderproduct.OrderProductRepository;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;
import com.study.cafekiosk.domain.product.ProductType;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
	}

	@DisplayName("주문 번호 목록을 받아 주문을 생성한다.")
	@Test
	void createOrder() {
		// given
		Product product1 = createProduct(HANDMADE, "001", 2000);
		Product product2 = createProduct(BAKERY, "002", 4000);
		Product product3 = createProduct(HANDMADE, "003", 3000);

		productRepository.saveAll(List.of(product1, product2, product3));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "002"))
			.build();
		LocalDateTime registeredAt = LocalDateTime.now();

		// when
		OrderResponse response = orderService.createOrder(request, registeredAt);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response)
			.extracting("registeredAt", "totalPrice")
			.contains(registeredAt, 6000);
		assertThat(response.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 2000),
				tuple("002", 4000)
			);
	}

	@DisplayName("중복되는 상품 번호 목록으로 주문을 생성할 수 있다.")
	@Test
	void createOrderWithDuplicatedProductNumbers() {
		// given
		Product product1 = createProduct(HANDMADE, "001", 2000);
		Product product2 = createProduct(BAKERY, "002", 4000);
		Product product3 = createProduct(HANDMADE, "003", 3000);

		productRepository.saveAll(List.of(product1, product2, product3));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "001"))
			.build();
		LocalDateTime registeredAt = LocalDateTime.now();

		// when
		OrderResponse response = orderService.createOrder(request, registeredAt);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response)
			.extracting("registeredAt", "totalPrice")
			.contains(registeredAt, 4000);
		assertThat(response.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 2000),
				tuple("001", 2000)
			);
	}

	private Product createProduct(ProductType type, String productNumber, int price) {
		return Product.builder()
			.type(type)
			.productNumber(productNumber)
			.price(price)
			.sellingStatus(SELLING)
			.name("메뉴_" + UUID.randomUUID())
			.build();
	}
}