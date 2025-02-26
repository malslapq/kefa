package com.kefa.domain.entity;

import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE accounts SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Account {

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
    private SubscriptionType subscriptionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private boolean verified;

    @Column
    private boolean isDeleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "account_login_types",
        joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<LoginType> loginTypes = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void addLoginType(LoginType loginType) {
        if (this.loginTypes == null) {
            this.loginTypes = new HashSet<>();
        }
        this.loginTypes.add(loginType);
    }

    public void verify() {
        this.verified = true;
    }

}