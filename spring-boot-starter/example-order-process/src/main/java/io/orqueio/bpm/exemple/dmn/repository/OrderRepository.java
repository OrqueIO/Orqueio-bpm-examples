package io.orqueio.bpm.exemple.dmn.repository;


import io.orqueio.bpm.exemple.dmn.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Use findFirst to handle existing duplicates, ordered by ID descending to get the most recent
    Order findFirstByOrderIdOrderByIdDesc(String orderId);
}
