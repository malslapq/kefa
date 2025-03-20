package com.kefa.domain.entity;

import com.kefa.domain.type.FeedbackType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 세션이나 문서의 id
    @Column(nullable = false)
    private Long targetId;

    // 댓글 단 회원 id
    @Column(nullable = false)
    private Long accountId;

    // 대댓글
    @Column
    private Long parentId;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
