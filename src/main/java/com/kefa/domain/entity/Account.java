package com.kefa.domain.entity;

import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "account_login_types",
        joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<LoginType> loginTypes = new HashSet<>();

    public void addLoginType(LoginType loginType) {
        this.loginTypes.add(loginType);
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