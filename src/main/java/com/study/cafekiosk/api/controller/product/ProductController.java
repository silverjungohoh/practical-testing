package com.study.cafekiosk.api.controller.product;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.cafekiosk.api.ApiResponse;
import com.study.cafekiosk.api.controller.product.request.ProductCreateRequest;
import com.study.cafekiosk.api.service.product.ProductService;
import com.study.cafekiosk.api.service.product.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping("/products/new")
	public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
		ProductResponse response = productService.createProduct(request);
		return ApiResponse.ok(response);
	}

	@GetMapping("/products/selling")
	public  ApiResponse<List<ProductResponse>> getSellingProducts() {
		List<ProductResponse> response = productService.getSellingProducts();
		return ApiResponse.ok(response);
	}
}
