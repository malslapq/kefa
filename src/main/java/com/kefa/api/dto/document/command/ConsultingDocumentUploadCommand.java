package com.kefa.api.dto.document.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultingDocumentUploadCommand {

    private Long companyId;
    private Long consultingSessionId;
    private Long loginAccountId;
    private List<MultipartFile> files;

}
