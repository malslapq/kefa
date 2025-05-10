package com.kefa.domain.entity;

import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private boolean emailVerified;

    @OneToOne(fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "account")
    @Builder.Default
    private List<SocialInfo> socialInfos = new ArrayList<>();

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        refreshToken.addAccount(this);
    }

    public void addSocialInfo(SocialInfo socialInfo) {
        this.socialInfos.add(socialInfo);
    }

    public void removeSocialInfo(SocialInfo socialInfo) {
        this.socialInfos.remove(socialInfo);
    }

    public void verify() {
        this.emailVerified = true;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}