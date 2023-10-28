package com.study.cafekiosk.api.service.order;

import static com.study.cafekiosk.domain.order.OrderStatus.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.study.cafekiosk.api.service.mail.MailService;
import com.study.cafekiosk.domain.order.Order;
import com.study.cafekiosk.domain.order.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

	private final OrderRepository orderRepository;
	private final MailService mailService;

	public boolean sendOrderStatisticsMail(LocalDate orderDate, String email) {
		// TODO : 해당일자에 결제 완료된 주문들 가져오기
		List<Order> orders = orderRepository.findOrdersBy(
			orderDate.atStartOfDay(),
			orderDate.plusDays(1).atStartOfDay(),
			PAYMENT_COMPLETED
		);
		// TODO : 총 매출 합계 계산
		int totalAmount = orders.stream()
			.mapToInt(Order::getTotalPrice)
			.sum();

		// TODO : 메일 전송
		boolean result = mailService.send(
			"test@test.com", email,
			String.format("[%s 매출 통계 내역서]", orderDate),
			String.format("매출 총액은 %d원 입니다.", totalAmount)
		);

		if(!result) {
			throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
		}
		return true;
	}
}
