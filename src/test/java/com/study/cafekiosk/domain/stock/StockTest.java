package com.study.cafekiosk.domain.stock;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

	@DisplayName("재고의 수량이 인자로 전달된 수량보다 작은지 확인한다.")
	@Test
	void isQuantityLessThan() {
		// given
		Stock stock = Stock.create("001", 3);
		// when
		boolean result = stock.isQuantityLessThan(4);
		// then
		assertThat(result).isTrue();
	}

	@DisplayName("재고의 수량을 주어진 수량만큼 차감할 수 있다.")
	@Test
	void deductQuantity() {
		// given
		Stock stock = Stock.create("001", 4);
		// when
		stock.deductQuantity(4);
		// then
		assertThat(stock.getQuantity()).isEqualTo(0);
	}

	@DisplayName("재고보다 많은 수량으로 차감을 시도하는 경우 예외가 발생한다.")
	@Test
	void deductQuantityImpossible() {
		// given
		Stock stock = Stock.create("001", 2);
		// when & then
		assertThatThrownBy(() -> stock.deductQuantity(4))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("차감할 재고 수량이 없습니다.");
	}
}