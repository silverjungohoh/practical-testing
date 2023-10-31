package com.study.cafekiosk.api.controller.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.study.cafekiosk.ControllerTestSupport;
import com.study.cafekiosk.api.controller.order.request.OrderCreateRequest;

class OrderControllerTest extends ControllerTestSupport {

	@DisplayName("신규 주문을 생성한다.")
	@Test
	void createOrder() throws Exception {
		// given
		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "002", "003"))
			.build();

		// when & then
		mockMvc.perform(post("/api/v1/orders/new")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("OK"))
			.andDo(print());
	}

	@DisplayName("신규 주문 생성 시 상품 번호가 하나 이상 존재해야 한다.")
	@Test
	void createOrderWithoutProductNumbers() throws Exception {
		// given
		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of())
			.build();

		// when & then
		mockMvc.perform(
				post("/api/v1/orders/new")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("상품 번호 목록은 필수입니다."))
			.andExpect(jsonPath("$.data").isEmpty())
			.andDo(print());
	}
}