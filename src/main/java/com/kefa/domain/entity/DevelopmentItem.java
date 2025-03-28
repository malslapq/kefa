package com.kefa.domain.entity;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemUpdateRequest;
import com.kefa.domain.type.Stage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "development_items")
@Entity
public class DevelopmentItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private String technologyField;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public void update(DevelopmentItemUpdateRequest request) {
        this.name = request.getName();
        this.stage = request.getStage();
        this.technologyField = request.getTechnologyField();
        this.description = request.getDescription();
    }

    public static DevelopmentItem of(DevelopmentItemAddRequest request, Company company) {
        return DevelopmentItem.builder()
            .name(request.getName())
            .stage(request.getStage())
            .technologyField(request.getTechnologyField())
            .description(request.getDescription())
            .company(company)
            .build();
    }

}
