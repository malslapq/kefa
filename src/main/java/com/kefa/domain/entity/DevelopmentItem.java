package com.kefa.domain.entity;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
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
    private String technologyField;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // 회사와의 관계 설정
    @JoinColumn(name = "company_id", nullable = false) // 외래 키 이름
    private Company company;

    public static DevelopmentItem from(DevelopmentItemAddRequest request) {
        return DevelopmentItem.builder()
            .name(request.getName())
            .stage(request.getStage())
            .technologyField(request.getTechnologyField())
            .description(request.getDescription())
            .build();
    }

}
