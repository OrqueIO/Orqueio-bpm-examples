/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.orqueio.bpm.exemple.dmn;

import io.orqueio.bpm.engine.RuntimeService;
import io.orqueio.bpm.exemple.dmn.service.OrderService;
import io.orqueio.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import io.orqueio.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableProcessApplication
public class OrderApplication {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private OrderService orderService;

    public static void main(String... args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @EventListener
    public void onPostDeploy(PostDeployEvent event) {
        String orderId = "ORD-101";

        if (orderService.getOrderByOrderId(orderId).isPresent()) {
            System.out.println("Order " + orderId + " already exists. Skipping process start.");
            return;
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("orderId", orderId);
        vars.put("premium", "High");
        vars.put("status", "VIP");

        runtimeService.startProcessInstanceByKey("OrderProcess", vars);
        System.out.println("Processus OrderProcess démarré avec les variables : " + vars);
    }
}