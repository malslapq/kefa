package com.kefa.domain.entity;

import com.kefa.common.type.OrderStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToOne
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;



}
