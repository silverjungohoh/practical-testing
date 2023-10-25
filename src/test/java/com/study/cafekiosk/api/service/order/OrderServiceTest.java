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
import com.study.cafekiosk.domain.stock.Stock;
import com.study.cafekiosk.domain.stock.StockRepository;

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
	@Autowired
	private StockRepository stockRepository;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		stockRepository.deleteAllInBatch();
	}

	@DisplayName("주문 번호 목록을 받아 주문을 생성한다.")
	@Test
	void createOrder() {
		// given
		Product product1 = createProduct(HANDMADE, "001", 2000);
		Product product2 = createProduct(HANDMADE, "002", 4000);
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

	@DisplayName("재고와 관련된 상품이 포함되어 있는 주문 번호 목록을 받아 주문을 생성한다.")
	@Test
	void createOrderWithStock() {
		// given
		LocalDateTime registeredAt = LocalDateTime.now();
		Product product1 = createProduct(BOTTLE, "001", 2000);
		Product product2 = createProduct(BAKERY, "002", 4000);
		Product product3 = createProduct(HANDMADE, "003", 3000);
		productRepository.saveAll(List.of(product1, product2, product3));

		Stock stock1 = Stock.create("001", 2);
		Stock stock2 = Stock.create("002", 2);
		stockRepository.saveAll(List.of(stock1, stock2));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "001", "002", "003"))
			.build();

		// when
		OrderResponse response = orderService.createOrder(request, registeredAt);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response)
			.extracting("registeredAt", "totalPrice")
			.contains(registeredAt, 11000);
		assertThat(response.getProducts()).hasSize(4)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 2000),
				tuple("001", 2000),
				tuple("002", 4000),
				tuple("003", 3000)
			);
		List<Stock> stocks = stockRepository.findAll();
		assertThat(stocks).hasSize(2)
			.extracting("productNumber", "quantity")
			.containsExactlyInAnyOrder(
				tuple("001", 0),
				tuple("002", 1)
			);
	}

	@DisplayName("재고가 부족한 상품으로 주문을 생성하는 경우 예외가 발생한다.")
	@Test
	void createOrderWithNoStock() {
		// given
		LocalDateTime registeredAt = LocalDateTime.now();
		Product product1 = createProduct(BOTTLE, "001", 2000);
		Product product2 = createProduct(BAKERY, "002", 4000);
		Product product3 = createProduct(HANDMADE, "003", 3000);
		productRepository.saveAll(List.of(product1, product2, product3));

		Stock stock1 = Stock.create("001", 0);
		Stock stock2 = Stock.create("002", 2);
		stockRepository.saveAll(List.of(stock1, stock2));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "001", "002"))
			.build();

		// when & then
		assertThatThrownBy(() -> orderService.createOrder(request, registeredAt))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("재고가 부족한 상품이 포함되어 있습니다.");
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