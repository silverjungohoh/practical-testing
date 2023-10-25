package com.study.cafekiosk.domain.product;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTypeTest {

	@DisplayName("상품 타입이 재고와 관련된 타입인지 확인한다.")
	@Test
	void containsStockType() {
		// given
		ProductType type1 = ProductType.HANDMADE;
		ProductType type2 = ProductType.BAKERY;
		// when
		boolean result1 = ProductType.containsStockType(type1);
		boolean result2 = ProductType.containsStockType(type2);
		// then
		assertThat(result1).isFalse();
		assertThat(result2).isTrue();
	}
}