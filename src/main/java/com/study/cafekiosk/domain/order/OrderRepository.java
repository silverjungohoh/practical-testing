package com.study.cafekiosk.domain.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query(value = "select o from Order o "
		+ "where o.registeredAt >= :start and o.registeredAt < :end"
		+ " and o.orderStatus = :orderStatus"
	)
	List<Order> findOrdersBy(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
		@Param("orderStatus") OrderStatus orderStatus);
}
