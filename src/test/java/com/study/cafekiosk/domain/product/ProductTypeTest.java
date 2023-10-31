package com.study.cafekiosk.domain.product;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProductTypeTest {

	@DisplayName("상품 타입이 재고와 관련된 타입인지 확인한다.")
	@Test
	void containsStockType1() {
		// given
		ProductType type = ProductType.HANDMADE;
		// when
		boolean result = ProductType.containsStockType(type);
		// then
		assertThat(result).isFalse();
	}

	@DisplayName("상품 타입이 재고와 관련된 타입인지 확인한다.")
	@Test
	void containsStockType2() {
		// given
		ProductType type = ProductType.BAKERY;
		// when
		boolean result = ProductType.containsStockType(type);
		// then
		assertThat(result).isTrue();
	}

	@DisplayName("상품 타입이 재고와 관련된 타입인지 확인한다.")
	@CsvSource({"HANDMADE, false", "BOTTLE, true", "BAKERY, true"})
	@ParameterizedTest
	void containsStockType(ProductType productType, boolean expected) {
		// when
		boolean result = ProductType.containsStockType(productType);
		// then
		assertThat(result).isEqualTo(expected);
	}
}