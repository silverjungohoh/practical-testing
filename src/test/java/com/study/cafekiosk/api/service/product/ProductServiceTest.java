package com.study.cafekiosk.api.service.product;

import static com.study.cafekiosk.domain.product.ProductSellingStatus.*;
import static com.study.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.study.cafekiosk.IntegrationTestSupport;
import com.study.cafekiosk.api.controller.product.request.ProductCreateRequest;
import com.study.cafekiosk.api.service.product.response.ProductResponse;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;
import com.study.cafekiosk.domain.product.ProductSellingStatus;

class ProductServiceTest extends IntegrationTestSupport {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		// 각 테스트 입장에서 봤을 때, 아예 몰라도 테스트 내용을 이해하는데 문제가 없는가
		// 수정해도 모든 테스트에 영향을 주지 않는가?
	}

	@AfterEach
	void tearDown() {
		productRepository.deleteAllInBatch();
	}

	@DisplayName("신규 상품을 등록한다. 상품 번호는 가장 최근 상품의 상품 번호에서 1 증가한 값이다.")
	@Test
	void createProduct() {
		// given
		Product product1 = createProduct("001", SELLING, 4000, "아메리카노");
		Product product2 = createProduct("002", HOLD, 4500, "카페라떼");
		productRepository.saveAll(List.of(product1, product2));

		ProductCreateRequest request = ProductCreateRequest.builder()
			.type(HANDMADE)
			.sellingStatus(SELLING)
			.name("연유라떼")
			.price(5000)
			.build();

		// when
		ProductResponse response = productService.createProduct(request);
		// then
		assertThat(response)
			.extracting("productNumber", "sellingStatus", "name", "price")
			.contains("003", SELLING, "연유라떼", 5000);

		List<Product> products = productRepository.findAll();
		assertThat(products).hasSize(3)
			.extracting("productNumber", "sellingStatus", "name", "price")
			.containsExactlyInAnyOrder(
				tuple("001", SELLING, "아메리카노", 4000),
				tuple("002", HOLD, "카페라떼", 4500),
				tuple("003", SELLING, "연유라떼", 5000)
			);
	}

	@DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품 번호는 001이다.")
	@Test
	void createProductWhenProductIsEmpty() {
		// given
		ProductCreateRequest request = ProductCreateRequest.builder()
			.type(HANDMADE)
			.sellingStatus(SELLING)
			.name("연유라떼")
			.price(5000)
			.build();

		// when
		ProductResponse response = productService.createProduct(request);

		// then
		assertThat(response)
			.extracting("productNumber", "type", "sellingStatus", "name", "price")
			.contains("001", SELLING, "연유라떼", 5000);

		List<Product> products = productRepository.findAll();
		assertThat(products).hasSize(1)
			.extracting("productNumber", "type", "sellingStatus", "name", "price")
			.contains(
				tuple("001", SELLING, "연유라떼", 5000)
			);
	}

	private Product createProduct(String num, ProductSellingStatus status, int price, String name) {
		return Product.builder()
			.productNumber(num)
			.type(HANDMADE)
			.sellingStatus(status)
			.price(price)
			.name(name)
			.build();
	}
}