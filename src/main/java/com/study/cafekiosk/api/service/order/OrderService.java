package com.study.cafekiosk.api.service.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.cafekiosk.api.controller.order.request.OrderCreateRequest;
import com.study.cafekiosk.api.service.order.response.OrderResponse;
import com.study.cafekiosk.domain.order.Order;
import com.study.cafekiosk.domain.order.OrderRepository;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;
import com.study.cafekiosk.domain.product.ProductType;
import com.study.cafekiosk.domain.stock.Stock;
import com.study.cafekiosk.domain.stock.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	/**
	 * 재고 감소 >> 동시성 고민
	 * optimistic lock / pessimistic lock / ...
	 */

	public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredAt) {
		List<String> productNumbers = request.getProductNumbers();
		List<Product> products = findProductsBy(productNumbers);

		deductStockQuantities(products);
		Order order = Order.create(products, registeredAt);
		Order savedOrder = orderRepository.save(order);
		return OrderResponse.from(savedOrder);
	}

	private void deductStockQuantities(List<Product> products) {
		List<String> stockProductNumbers = extractStockProductNumbers(products);
		// TODO : 재고 entity 조회
		List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
		Map<String, Stock> stockMap = createStockMapBy(stocks);
		Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);
		// TODO : 재고 차감 시도
		for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
			Stock stock = stockMap.get(stockProductNumber);
			int quantity = productCountingMap.get(stockProductNumber).intValue();

			if(stock.isQuantityLessThan(quantity)) {
				throw new IllegalArgumentException("재고가 부족한 상품이 포함되어 있습니다.");
			}
			stock.deductQuantity(quantity);
		}
	}

	// TODO : 상품별 counting
	private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
		return stockProductNumbers.stream()
			.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
	}

	private static Map<String, Stock> createStockMapBy(List<Stock> stocks) {
		return stocks.stream()
			.collect(Collectors.toMap(Stock::getProductNumber, s -> s));
	}

	// TODO : 재고 차감 확인이 필요한 상품들
	private static List<String> extractStockProductNumbers(List<Product> products) {
		return products.stream()
			.filter(product -> ProductType.containsStockType(product.getType()))
			.map(Product::getProductNumber)
			.collect(Collectors.toList());
	}

	private List<Product> findProductsBy(List<String> productNumbers) {
		List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

		Map<String, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product::getProductNumber, p -> p));

		return productNumbers.stream()
			.map(productMap::get)
			.collect(Collectors.toList());
	}
}
