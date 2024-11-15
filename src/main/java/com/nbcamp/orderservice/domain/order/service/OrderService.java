package com.nbcamp.orderservice.domain.order.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.repository.CategoryJpaRepository;
import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.repository.OrderProductJpaRepository;
import com.nbcamp.orderservice.domain.order.repository.OrderQueryRepository;
import com.nbcamp.orderservice.domain.order.repository.OrderRepository;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.domain.product.repository.ProductJpaRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.store.repository.StoreQueryRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderProductJpaRepository orderProductJpaRepository;
	private final StoreJpaRepository storeJpaRepository;
	private final ProductJpaRepository productJpaRepository;
	private final OrderQueryRepository orderQueryRepository;
	private final OrderProductService orderProductService;
	private final StoreQueryRepository storeQueryRepository;
	private final CategoryJpaRepository categoryJpaRepository;

	@Transactional
	public OrderInfoResponse createOrder(OrderRequest request, User user) {
		validateUserRoleForCreateOrder(user);
		// TODO: 2024-11-15  주소 검증로직추가 (오프라인 온라인) 오프라인은 주소가 없음
		Store store = getStoreById(request.storeId());

		Order order = Order.create(request, store, user);

		List<OrderProduct> orderProducts = orderProductService.createOrderProducts(order, request.products());
		order.addOrderProduct(orderProducts);
		orderRepository.save(order);

		OrderResponse orderResponse = new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);

		List<OrderProductResponse> orderProductResponses = orderProducts.stream()
			.map(orderProduct -> new OrderProductResponse(
				orderProduct.getId(),
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getTotalPrice()
			))
			.toList();

		return new OrderInfoResponse(orderResponse, orderProductResponses);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersAdmin(
		Pageable pageable,
		UUID storeId,
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption
	) {
		return orderQueryRepository.findByStoreOrders(
			pageable,
			storeId,
			orderType,
			startDate,
			endDate,
			orderStatus,
			sortOption
		);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersCustomer(
		Pageable pageable,
		User user,
		String storeName,
		UUID categoryId,
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption
	) {
		if(categoryId != null){
			existsByCategory(categoryId);
		}
		return orderQueryRepository.findAllByUserOrder(
			pageable,
			user,
			storeName,
			categoryId,
			orderType,
			startDate,
			endDate,
			orderStatus,
			sortOption
		);
	}


	private Store getStoreById(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

	private void validateUserRoleForCreateOrder(User user) {
		if (user.getUserRole() != UserRole.CUSTOMER && user.getUserRole() != UserRole.OWNER) {
			throw new IllegalArgumentException(ErrorCode.NO_PERMISSION_TO_CREATE_ORDER.getMessage());
		}
	}

	private Product getProductById(String productId) {
		return productJpaRepository.findById(UUID.fromString(productId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	}

	private List<Product> getProductsFromRequest(List<OrderRequest.OrderProduct> productRequests) {
		return productRequests.stream()
			.map(productRequest -> getProductById(productRequest.productId().toString()))
			.collect(Collectors.toList());
	}

	@Transactional
	public void cancelOrder(String orderId, User user) {
		Order order = orderRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		if (order.getDeletedAt() != null) {
			throw new IllegalStateException(ErrorCode.ALREADY_CANCELED.getMessage());
		}

		if (user.getUserRole() == UserRole.CUSTOMER) {
			LocalDateTime orderTime = order.getCreatedAt();
			LocalDateTime now = LocalDateTime.now();
			if (Duration.between(orderTime, now).toMinutes() > 5) {
				throw new IllegalStateException(ErrorCode.CANCELLATION_TIME_EXCEEDED.getMessage());
			}
		}

		order.cancelOrder(user.getId());
	}

	public OrderInfoResponse getOrderDetail(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		List<OrderProduct> orderProducts = orderQueryRepository.findOrderProductsBy(orderId);

		OrderResponse orderResponse = new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);

		List<OrderProductResponse> orderProductResponses = orderProducts.stream()
			.map(orderProduct -> new OrderProductResponse(
				orderProduct.getId(),
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getTotalPrice()
			))
			.toList();

		return new OrderInfoResponse(orderResponse, orderProductResponses);
		return null;
	}

	public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		validateOrderStatus(order.getOrderStatus());

		order.updateOrderStatus(newStatus);
		orderRepository.save(order);

		// 업데이트된 OrderResponse 반환
		return new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);
	}

	private void validateOrderStatus(OrderStatus currentStatus) {
		if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ORDER_STATUS.getMessage());
		}
	}


	private void existsByCategory(UUID categoryId){
		if(!categoryJpaRepository.existsById(categoryId)){
			throw new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage());
		}
	}

}
