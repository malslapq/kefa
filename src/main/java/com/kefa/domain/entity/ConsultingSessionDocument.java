package com.kefa.domain.entity;

import com.kefa.infrastructure.aws.dto.SaveFileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ConsultingSessionDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long saveAccountId;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulting_session_id", nullable = false)
    private ConsultingSession consultingSession;

    @CreatedDate
    private LocalDateTime createdAt;

    public void addConsultingSession(ConsultingSession consultingSession) {
        this.consultingSession = consultingSession;
    }

    public static ConsultingSessionDocument of(SaveFileDto saveFileDto, Long saveAccountId){
        return ConsultingSessionDocument.builder()
            .saveAccountId(saveAccountId)
            .fileUrl(saveFileDto.getUrl())
            .fileName(saveFileDto.getName())
            .build();
    }

}
