package com.study.cafekiosk.domain.order;

import static com.study.cafekiosk.domain.order.OrderStatus.*;
import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.study.cafekiosk.domain.product.Product;

class OrderTest {

	@DisplayName("주문 생성 시 상품 목록에서 주문의 총 금액을 계산한다")
	@Test
	void calculateTotalPrice() {
		// given
		List<Product> products = List.of(
			createProduct("001", 2000),
			createProduct("002", 3000)
		);

		// when
		Order order = Order.create(products, LocalDateTime.now());

		// then
		assertThat(order.getTotalPrice()).isEqualTo(5000);
	}

	@DisplayName("주문 생성 시 주문 상태는 INIT")
	@Test
	void init() {
		// given
		List<Product> products = List.of(
			createProduct("001", 2000),
			createProduct("002", 3000)
		);

		// when
		Order order = Order.create(products, LocalDateTime.now());

		// then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(INIT);
	}

	@DisplayName("주문 생성 시 주문 등록 시간을 기록한다.")
	@Test
	void registeredAt() {
		// given
		LocalDateTime registeredAt = LocalDateTime.now();
		List<Product> products = List.of(
			createProduct("001", 2000),
			createProduct("002", 3000)
		);
		// when
		Order order = Order.create(products, registeredAt);
		// then
		assertThat(order.getRegisteredAt()).isEqualTo(registeredAt);
	}

	private Product createProduct(String productNumber, int price) {
		return Product.builder()
			.type(HANDMADE)
			.productNumber(productNumber)
			.price(price)
			.sellingStatus(SELLING)
			.name("메뉴_" + UUID.randomUUID())
			.build();
	}
}