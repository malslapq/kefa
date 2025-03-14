package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.type.ConsultingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultingSessionResponse {

    private Long id;
    private ConsultingStatus status;

    public static ConsultingSessionResponse from(ConsultingSession consultingSession) {
        return ConsultingSessionResponse.builder()
            .id(consultingSession.getId())
            .status(consultingSession.getStatus())
            .build();
    }

}
