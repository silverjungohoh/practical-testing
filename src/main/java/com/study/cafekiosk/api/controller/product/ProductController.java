package com.study.cafekiosk.api.controller.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.cafekiosk.api.service.product.ProductService;
import com.study.cafekiosk.api.service.product.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/products")
	public List<ProductResponse> getSellingProducts() {
		return productService.getSellingProducts();
	}
}