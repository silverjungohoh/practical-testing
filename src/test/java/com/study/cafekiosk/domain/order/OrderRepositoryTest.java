package com.study.cafekiosk.domain.order;

import static com.study.cafekiosk.domain.order.OrderStatus.*;
import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.study.cafekiosk.domain.orderproduct.OrderProductRepository;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;

@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryTest {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
	}

	@DisplayName("정해진 기간 동안 결제 완료된 주문 목록들을 조회한다.")
	@Test
	void findOrdersBy() {
		// given
		LocalDateTime start = LocalDateTime.of(2023, 10, 28, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2023, 10, 29, 0, 0, 0);

		Product product1 = createProduct("001", "바닐라라떼", 5000);
		Product product2 = createProduct("002", "카페라떼", 4000);
		Product product3 = createProduct("002", "카페모카", 5500);
		productRepository.saveAll(List.of(product1, product2, product3));

		Order order1 =  createOrder(start, List.of(product1, product2), PAYMENT_COMPLETED);
		Order order2 = createOrder(start, List.of(product1, product3), INIT);
		Order order3 =  createOrder(end, List.of(product2, product3), PAYMENT_COMPLETED);
		Order order4 =  createOrder(start, List.of(product1, product2, product3), PAYMENT_COMPLETED);
		orderRepository.saveAll(List.of(order1, order2, order3, order4));

		// when
		List<Order> orders = orderRepository.findOrdersBy(start, end, PAYMENT_COMPLETED);
		// then
		assertThat(orders).hasSize(2)
			.extracting("orderStatus", "totalPrice", "registeredAt")
			.containsExactlyInAnyOrder(
				tuple(PAYMENT_COMPLETED, 9000, start),
				tuple(PAYMENT_COMPLETED, 14500, start)
			);
	}

	private Product createProduct(String num, String name, int price) {
		return Product.builder()
			.productNumber(num)
			.type(HANDMADE)
			.sellingStatus(SELLING)
			.price(price)
			.name(name)
			.build();
	}

	private Order createOrder(LocalDateTime registeredAt, List<Product> products, OrderStatus orderStatus) {
		return Order.builder()
			.products(products)
			.orderStatus(orderStatus)
			.registeredAt(registeredAt)
			.build();
	}
}