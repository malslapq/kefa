package com.kefa.api.dto.consulting.command;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetConsultingSessionDocsCommand {

    private Long companyId;
    private Long consultingSessionId;
    private Long loginAccountId;
    private int page;
    private int size;

}
