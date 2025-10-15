package io.orqueio.bpm.exemple.dmn;

import io.orqueio.bpm.exemple.dmn.model.Order;
import io.orqueio.bpm.exemple.dmn.service.OrderService;
import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("checkStock")
public class CheckStockDelegate implements JavaDelegate {

    @Autowired
    private OrderService orderService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        String premium = (String) execution.getVariable("premium");
        String status = (String) execution.getVariable("status");

        boolean inStock = checkStockAvailability(orderId);
        execution.setVariable("inStock", inStock);
        Order order = new Order();
        order.setOrderId(orderId);
        order.setPremium(premium);
        order.setStatus(status);
        order.setInStock(inStock);
        orderService.saveOrder(order);
        System.out.println("Vérification stock pour la commande " + orderId + ": " + (inStock ? "En stock" : "Rupture"));
    }

    private boolean checkStockAvailability(String orderId) {
        return true;
    }
}
