package com.study.cafekiosk.domain.product;

import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.study.cafekiosk.IntegrationTestSupport;

@Transactional
class ProductRepositoryTest extends IntegrationTestSupport {

	@Autowired
	private ProductRepository productRepository;

	@DisplayName("원하는 판매 상태를 가진 상품들을 조회한다.")
	@Test
	void findAllBySellingStatusIn() {
		// given
		Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
		Product product2 = createProduct("002", HANDMADE, HOLD, 4500, "카페라떼");
		Product product3 = createProduct("003", HANDMADE, STOP_SELLING, 5000, "바닐라라떼");
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

	@DisplayName("상품 번호 목록으로 상품들을 조회한다.")
	@Test
	void findAllByProductNumberIn() {
		// given
		Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
		Product product2 = createProduct("002", HANDMADE, HOLD, 4500, "카페라떼");
		Product product3 = createProduct("003", HANDMADE, STOP_SELLING, 5000, "바닐라라떼");
		productRepository.saveAll(List.of(product1, product2, product3));

		// when
		List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

		// then
		assertThat(products).hasSize(2)
			.extracting("productNumber", "name", "sellingStatus")
			.containsExactlyInAnyOrder(
				tuple("001", "아메리카노", SELLING),
				tuple("002", "카페라떼", HOLD)
			);
	}

	@DisplayName("가장 마지막으로 저장한 상품의 상품 번호를 조회한다.")
	@Test
	void findLatestProductNumber() {
		// given
		String targetProductNumber = "003";
		Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
		Product product2 = createProduct("002", HANDMADE, HOLD, 4500, "카페라떼");
		Product product3 = createProduct("003", HANDMADE, STOP_SELLING, 5000, "바닐라라떼");
		productRepository.saveAll(List.of(product1, product2, product3));

		// when
		String result = productRepository.findLatestProductNumber();

		// then
		assertThat(result).isEqualTo(targetProductNumber);
	}

	@DisplayName("가장 마지막으로 저장한 상품의 상품 번호를 조회할 때 상품이 없어 null 반환한다.")
	@Test
	void findLatestProductNumberWhenProductIsEmpty() {
		// when
		String result = productRepository.findLatestProductNumber();
		// then
		assertThat(result).isNull();
	}

	private Product createProduct(String num, ProductType type, ProductSellingStatus status, int price, String name) {
		return Product.builder()
			.productNumber(num)
			.type(type)
			.sellingStatus(status)
			.price(price)
			.name(name)
			.build();
	}
}