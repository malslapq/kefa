package com.kefa.domain.entity;

import com.kefa.common.type.LoginType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class SocialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    /**
     * Associates this social login information with the specified account.
     *
     * @param account the account to link to this social login information
     */
    public void addAccount(Account account) {
        this.account = account;
    }

    /**
     * Dissociates this social login information from its linked account.
     */
    public void removeAccount() {
        this.account = null;
    }

}
