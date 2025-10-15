package io.orqueio.bpm.exemple.dmn;

import io.orqueio.bpm.exemple.dmn.model.Order;
import io.orqueio.bpm.exemple.dmn.service.OrderService;
import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sendNotification")
public class SendNotificationDelegate implements JavaDelegate {

    @Autowired
    private OrderService orderService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String notification = (String) execution.getVariable("notification");
        String orderId = (String) execution.getVariable("orderId");
        execution.setVariable("notificationSent", true);
        Order order = orderService.getOrderByOrderId(orderId).orElseThrow();
        order.setNotificationSent(true);
        orderService.updateOrder(order);
        System.out.println("Notification envoyée pour la commande " + orderId + ": " + notification);
    }
}
