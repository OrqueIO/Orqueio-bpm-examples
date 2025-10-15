package io.orqueio.bpm.exemple.dmn.model;


import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String premium;
    private String status;
    @Column(columnDefinition = "BIT", length = 1)
    private boolean inStock;
    @Column(columnDefinition = "BIT", length = 1)
    private boolean notificationSent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPremium() { return premium; }
    public void setPremium(String premium) { this.premium = premium; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public boolean isNotificationSent() { return notificationSent; }
    public void setNotificationSent(boolean notificationSent) { this.notificationSent = notificationSent; }
}

