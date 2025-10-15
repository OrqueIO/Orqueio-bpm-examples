package io.orqueio.bpm.exemple.dmn.service;

import io.orqueio.bpm.exemple.dmn.model.Order;
import io.orqueio.bpm.exemple.dmn.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderByOrderId(String orderId) {
        return Optional.ofNullable(orderRepository.findByOrderId(orderId));
    }

    public void updateOrder(Order order) {
        orderRepository.save(order);
    }
}
