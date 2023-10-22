package com.study.cafekiosk.api.service.product.response;

import com.study.cafekiosk.domain.product.Product;
import com.study.cafekiosk.domain.product.ProductSellingStatus;
import com.study.cafekiosk.domain.product.ProductType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

	private Long id;

	private String productNumber;

	private ProductType type;

	private ProductSellingStatus sellingStatus;

	private String name;

	private int price;


	public static ProductResponse from(Product product) {
		return ProductResponse.builder()
			.id(product.getId())
			.productNumber(product.getProductNumber())
			.name(product.getName())
			.type(product.getType())
			.sellingStatus(product.getSellingStatus())
			.price(product.getPrice())
			.build();
	}
}
