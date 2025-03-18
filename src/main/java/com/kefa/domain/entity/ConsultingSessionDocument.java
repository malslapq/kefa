package com.kefa.domain.entity;

import com.kefa.infrastructure.aws.dto.SaveFileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static ConsultingSessionDocument of(SaveFileDto saveFileDto, Long saveAccountId){
        return ConsultingSessionDocument.builder()
            .saveAccountId(saveAccountId)
            .fileUrl(saveFileDto.getUrl())
            .fileName(saveFileDto.getName())
            .build();
    }

}
