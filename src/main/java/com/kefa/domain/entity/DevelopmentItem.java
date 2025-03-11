package com.kefa.domain.entity;

import com.kefa.domain.type.Stage;
import jakarta.persistence.*;

@Entity
public class DevelopmentItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private String description;

}
