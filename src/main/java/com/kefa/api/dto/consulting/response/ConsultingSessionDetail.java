package com.kefa.api.dto.consulting.response;

import com.kefa.api.dto.document.response.ConsultingSessionDocumentDto;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionFeedback;
import com.kefa.domain.type.ConsultingStatus;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultingSessionDetail {

    private Long id;
    private Long companyId;
    private ConsultingStatus status;
    private List<ConsultingSessionDocumentDto> documents;
    private List<ConsultingSessionFeedbackDto> feedbacks;

    public static ConsultingSessionDetail from(ConsultingSession consultingSession) {
        return ConsultingSessionDetail.builder()
            .id(consultingSession.getId())
            .status(consultingSession.getStatus())
            .companyId(consultingSession.getCompany().getId())
            .documents(
                consultingSession.getDocuments()
                    .stream().map(ConsultingSessionDocumentDto::from).toList()
            )
            .build();
    }

    public static ConsultingSessionDetail of(ConsultingSession consultingSession, List<ConsultingSessionFeedback> consultingSessionFeedbacks) {
        return ConsultingSessionDetail.builder()
            .id(consultingSession.getId())
            .status(consultingSession.getStatus())
            .companyId(consultingSession.getCompany().getId())
            .documents(
                consultingSession.getDocuments()
                    .stream().map(ConsultingSessionDocumentDto::from).toList()
            )
            .feedbacks(consultingSessionFeedbacks.stream().map(ConsultingSessionFeedbackDto::from).toList())
            .build();
    }
}
