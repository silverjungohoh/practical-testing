package com.study.cafekiosk.api.service.order;

import static com.study.cafekiosk.domain.order.OrderStatus.*;
import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.study.cafekiosk.IntegrationTestSupport;
import com.study.cafekiosk.domain.history.mail.MailSendHistory;
import com.study.cafekiosk.domain.history.mail.MailSendHistoryRepository;
import com.study.cafekiosk.domain.order.Order;
import com.study.cafekiosk.domain.order.OrderRepository;
import com.study.cafekiosk.domain.orderproduct.OrderProductRepository;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;
import com.study.cafekiosk.domain.product.ProductType;

class OrderStatisticsServiceTest extends IntegrationTestSupport {

	@Autowired
	private OrderStatisticsService orderStatisticsService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private MailSendHistoryRepository mailSendHistoryRepository;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		mailSendHistoryRepository.deleteAllInBatch();
	}

	@DisplayName("결제 완료된 주문들을 조회하여 매출 통계 메일을 전송한다.")
	@Test
	void sendOrderStatisticsMail() {
		// given
		LocalDateTime now = LocalDateTime.of(2023, 10, 28, 0, 0);
		Product product1 = createProduct(HANDMADE, "001", 1000);
		Product product2 = createProduct(BAKERY, "002", 2000);
		Product product3 = createProduct(HANDMADE, "003", 3000);

		List<Product> products = List.of(product1, product2, product3);
		productRepository.saveAll(products);

		createPaymentCompletedOrder(LocalDateTime.of(2023, 10, 27, 23, 59), products);
		createPaymentCompletedOrder(now, products);
		createPaymentCompletedOrder(LocalDateTime.of(2023, 10, 28, 23, 59), products);
		createPaymentCompletedOrder(LocalDateTime.of(2023, 10, 29, 0, 0), products);

		// stubbing
		Mockito.when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

		// when
		boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 10, 28), "test@test.com");

		// then
		assertThat(result).isTrue();

		List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
		assertThat(histories).hasSize(1)
			.extracting("content")
			.contains("매출 총액은 12000원 입니다.");
	}

	private void createPaymentCompletedOrder(LocalDateTime now, List<Product> products) {
		Order order = Order.builder()
			.products(products)
			.orderStatus(PAYMENT_COMPLETED)
			.registeredAt(now)
			.build();
		orderRepository.save(order);
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