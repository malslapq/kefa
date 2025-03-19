package com.kefa.infrastructure.aws.dto;

import com.kefa.domain.entity.ConsultingSessionDocument;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class SaveFileDto {

    private Long id;
    private String url;
    private String name;
    private LocalDateTime createdAt;

    public static SaveFileDto from(ConsultingSessionDocument consultingSessionDocument) {
        return SaveFileDto.builder()
            .id(consultingSessionDocument.getId())
            .url(consultingSessionDocument.getFileUrl())
            .name(consultingSessionDocument.getFileName())
            .createdAt(consultingSessionDocument.getCreatedAt())
            .build();
    }

}
