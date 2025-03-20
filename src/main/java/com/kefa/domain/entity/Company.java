package com.kefa.domain.entity;

import com.kefa.api.dto.company.request.CompanyUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "companies")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<DevelopmentItem> developmentItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ConsultingSession> consultingSessions = new ArrayList<>();

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String businessNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String industry;

    @Column(nullable = false)
    private Long revenueMillion;

    public void update(CompanyUpdateRequest request) {
        this.name = request.getName();
        this.address = request.getAddress();
        this.industry = request.getIndustry();
        this.revenueMillion = request.getRevenueMillion();
    }

    public void updateBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public void addDevelopmentItem(DevelopmentItem item) {
        this.developmentItems.add(item);
    }

    public void addConsultingSession(ConsultingSession session) {
        this.consultingSessions.add(session);
    }

}
