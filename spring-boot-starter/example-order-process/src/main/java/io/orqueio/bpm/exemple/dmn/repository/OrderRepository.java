package io.orqueio.bpm.exemple.dmn.repository;


import io.orqueio.bpm.exemple.dmn.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(String orderId);
}
