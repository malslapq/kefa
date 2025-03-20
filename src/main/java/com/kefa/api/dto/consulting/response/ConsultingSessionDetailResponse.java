package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.type.ConsultingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultingSessionDetailResponse {

    private Long id;
    private List<ConsultingSessionDocumentDto> documents;
    private ConsultingStatus status;

    public static ConsultingSessionDetailResponse from(ConsultingSession consultingSession){
        return ConsultingSessionDetailResponse.builder()
            .id(consultingSession.getId())
            .status(consultingSession.getStatus())
            .documents(
                consultingSession.getDocuments()
                    .stream().map(ConsultingSessionDocumentDto::from).toList()
            )
            .build();
    }
}
