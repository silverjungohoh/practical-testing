package com.study.cafekiosk.api.controller.order.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

	@NotEmpty(message = "상품 번호 목록은 필수입니다.")
	private List<String> productNumbers;

	@Builder
	private OrderCreateRequest(List<String> productNumbers) {
		this.productNumbers = productNumbers;
	}
}
