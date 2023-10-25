package com.study.cafekiosk.api.service.product;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.cafekiosk.api.controller.product.request.ProductCreateRequest;
import com.study.cafekiosk.api.service.product.response.ProductResponse;
import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductRepository;
import com.study.cafekiosk.domain.product.ProductSellingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<ProductResponse> getSellingProducts() {
		List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

		return products
			.stream()
			.map(ProductResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public ProductResponse createProduct(ProductCreateRequest request) {
		String newProductNumber = createNewProductNumber();

		Product product = request.toEntity(newProductNumber);

		Product savedProduct = productRepository.save(product);
		return ProductResponse.from(savedProduct);
	}

	private String createNewProductNumber() {
		String latestProductNumber = productRepository.findLatestProductNumber();
		if(Objects.isNull(latestProductNumber)) {
			return "001";
		}
		int nextProductNumberInt = Integer.parseInt(latestProductNumber) + 1;

		return String.format("%03d", nextProductNumberInt);
	}
}
