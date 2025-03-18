package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.ConsultingSessionDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ConsultingSessionDocumentDto {

    private Long id;
    private String fileUrl;
    private String fileName;

    public static ConsultingSessionDocumentDto from(ConsultingSessionDocument consultingSessionDocument) {
        return ConsultingSessionDocumentDto.builder()
            .id(consultingSessionDocument.getId())
            .fileUrl(consultingSessionDocument.getFileUrl())
            .fileName(consultingSessionDocument.getFileName())
            .build();
    }

}
