package com.kefa.domain.entity;

import com.kefa.api.dto.notice.request.NoticeCreateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE notices SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private boolean isPinned;

    public void update(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }

    public static Notice of(NoticeCreateRequestDto dto, Account account) {
        return Notice.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .isPinned(dto.isPinned())
            .account(account)
            .build();
    }

}
