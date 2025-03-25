package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.ConsultingSessionDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultingSessionDocumentDto {

    private Long id;
    private String fileUrl;
    private String name;
    private FeedbackResponse feedback;

    public static ConsultingSessionDocumentDto from(ConsultingSessionDocument consultingSessionDocument) {
        return ConsultingSessionDocumentDto.builder()
            .id(consultingSessionDocument.getId())
            .fileUrl(consultingSessionDocument.getFileUrl())
            .name(consultingSessionDocument.getName())
            .build();
    }

}
