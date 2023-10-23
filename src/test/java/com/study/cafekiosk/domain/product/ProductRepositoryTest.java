package com.study.cafekiosk.domain.product;

import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@DisplayName("원하는 판매 상태를 가진 상품들을 조회한다.")
	@Test
	void findAllBySellingStatusIn() {
		// given
		Product product1 = Product.builder()
			.productNumber("001")
			.type(HANDMADE)
			.sellingStatus(SELLING)
			.price(4000)
			.name("아메리카노")
			.build();
		Product product2 = Product.builder()
			.productNumber("002")
			.type(HANDMADE)
			.sellingStatus(HOLD)
			.price(4500)
			.name("카페라떼")
			.build();
		Product product3 = Product.builder()
			.productNumber("003")
			.type(HANDMADE)
			.sellingStatus(STOP_SELLING)
			.price(5000)
			.name("바닐라라떼")
			.build();
		productRepository.saveAll(List.of(product1, product2, product3));

		// when
		List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

		// then
		assertThat(products).hasSize(2)
			.extracting("productNumber", "name", "sellingStatus")
			.containsExactlyInAnyOrder(
				tuple("001", "아메리카노", SELLING),
				tuple("002", "카페라떼", HOLD)
			);
	}
}