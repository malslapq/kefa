package com.kefa.api.dto.document.response;

import com.kefa.domain.entity.ConsultingSessionDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultingSessionDocumentDto {

    private Long id;
    private String fileUrl;
    private String name;
    private List<DocumentFeedbackDto> documentFeedbackDto;

    public static ConsultingSessionDocumentDto from(ConsultingSessionDocument consultingSessionDocument) {
        return ConsultingSessionDocumentDto.builder()
            .id(consultingSessionDocument.getId())
            .fileUrl(consultingSessionDocument.getFileUrl())
            .name(consultingSessionDocument.getName())
            .build();
    }

}
