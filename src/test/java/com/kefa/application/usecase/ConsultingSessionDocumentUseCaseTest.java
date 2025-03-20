package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.request.UpdateDocNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.ConsultingSessionDocumentException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.aws.s3.S3Service;
import com.kefa.infrastructure.repository.ConsultingSessionDocumentRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConsultingSessionDocumentUseCaseTest {

    @InjectMocks
    private ConsultingSessionDocumentUseCase useCase;

    @Mock
    private S3Service s3Service;

    @Mock
    private ConsultingSessionDocumentRepository consultingSessionDocumentRepository;

    @Mock
    private ConsultingSessionRepository consultingSessionRepository;

    private ConsultingSessionDocument consultingSessionDocument;
    private final Long targetId = 1L;

    @BeforeEach
    void setUp() {
        consultingSessionDocument = ConsultingSessionDocument.builder()
            .id(targetId)
            .name("testDocument")
            .saveAccountId(targetId)
            .build();
    }

    @DisplayName("문서 업로드 성공")
    @Test
    void uploadFilesSuccess() {
        // given
        List<SaveFileDto> mockFileDtos = List.of(new SaveFileDto());

        given(s3Service.uploadFile(any())).willReturn(mockFileDtos);

        // when
        List<SaveFileDto> result = useCase.uploadFiles(List.of());

        // then
        assertThat(result).isEqualTo(mockFileDtos);
    }

    @DisplayName("문서 삭제 성공")
    @Test
    void deleteDocumentSuccess() {
        // given
        given(consultingSessionDocumentRepository.findById(targetId)).willReturn(Optional.of(consultingSessionDocument));

        // when
        useCase.delete(targetId, targetId);

        // then
        verify(consultingSessionDocumentRepository).deleteById(targetId);
    }

    @DisplayName("문서 삭제 실패 - 문서 없음")
    @Test
    void deleteDocumentFailNotFound() {
        // given
        given(consultingSessionDocumentRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> useCase.delete(targetId, targetId))
            .isInstanceOf(ConsultingSessionDocumentException.class)
            .hasMessage(ErrorCode.NOT_FOUND_DOCUMENT.getMessage());
    }

    @DisplayName("문서 이름 업데이트 성공")
    @Test
    void updateDocumentNameSuccess() {
        // given
        UpdateDocNameRequest request = UpdateDocNameRequest.builder()
            .name("updatedName")
            .build();

        given(consultingSessionDocumentRepository.findById(targetId)).willReturn(Optional.of(consultingSessionDocument));

        // when
        useCase.updateName(targetId, targetId, request);

        // then
        assertThat(consultingSessionDocument.getName()).isEqualTo(request.getName());
    }

    @DisplayName("문서 이름 업데이트 실패 - 문서 없음")
    @Test
    void updateDocumentNameFailNotFound() {
        // given
        UpdateDocNameRequest request = UpdateDocNameRequest.builder()
            .name("updatedName")
            .build();

        given(consultingSessionDocumentRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> useCase.updateName(targetId, targetId, request))
            .isInstanceOf(ConsultingSessionDocumentException.class)
            .hasMessage(ErrorCode.NOT_FOUND_DOCUMENT.getMessage());
    }

    @DisplayName("문서 이름 업데이트 실패 - 소유권 없음")
    @Test
    void updateDocumentNameFailUnauthorized() {
        // given
        UpdateDocNameRequest request = UpdateDocNameRequest.builder()
            .name("updatedName")
            .build();

        // Mocking the consultingSessionDocument
        ConsultingSessionDocument consultingSessionDocument = mock(ConsultingSessionDocument.class);

        given(consultingSessionDocumentRepository.findById(targetId)).willReturn(Optional.of(consultingSessionDocument));
        given(consultingSessionDocument.getSaveAccountId()).willReturn(2L); // 다른 사용자

        // when & then
        assertThatThrownBy(() -> useCase.updateName(targetId, targetId, request))
            .isInstanceOf(ConsultingSessionDocumentException.class)
            .hasMessage(ErrorCode.UNAUTHORIZED_DOCUMENT_EDIT.getMessage());
    }

    @DisplayName("문서 조회 성공")
    @Test
    void getDocsSuccess() {
        // given
        List<ConsultingSessionDocument> mockDocuments = List.of(consultingSessionDocument);
        Page<ConsultingSessionDocument> mockPage = new PageImpl<>(mockDocuments);

        given(consultingSessionDocumentRepository.findByConsultingSessionId(any(), any())).willReturn(mockPage);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PagedResponse<SaveFileDto> response = useCase.getDocs(targetId, pageRequest);

        // then
        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getName()).isEqualTo(consultingSessionDocument.getName());
    }
}