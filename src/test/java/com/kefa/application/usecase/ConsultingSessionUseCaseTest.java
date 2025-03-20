package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.common.exception.ConsultingSessionException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.Company;
import com.kefa.domain.type.ConsultingStatus;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import com.kefa.infrastructure.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConsultingSessionUseCaseTest {

    @InjectMocks
    private ConsultingSessionUseCase useCase;

    @Mock
    private ConsultingSessionRepository consultingSessionRepository;

    @Mock
    private CompanyRepository companyRepository;

    private ConsultingSession consultingSession;
    private final Long sessionId = 1L;
    private final Long companyId = 2L;
    private final Long differentCompanyId = 10L;

    @BeforeEach
    void setUp() {
        Company company = Company.builder()
            .id(companyId)
            .name("testCompany")
            .build();

        consultingSession = ConsultingSession.builder()
            .id(sessionId)
            .company(company)
            .status(ConsultingStatus.WAITING)
            .documents(List.of())
            .participantAccountIds(new HashSet<>() )
            .build();
    }

    @DisplayName("컨설팅 세션 상세 조회 성공")
    @Test
    void getDetailSuccess() {
        // given
        given(consultingSessionRepository.findByIdWithDocumentsAndCompany(sessionId)).willReturn(Optional.of(consultingSession));

        // when
        ConsultingSessionDetailResponse response = useCase.getDetail(companyId, sessionId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(consultingSession.getStatus());
    }

    @DisplayName("컨설팅 세션 상세 조회 실패 - 세션 없음")
    @Test
    void getDetailFailNotFound() {
        // given
        given(consultingSessionRepository.findByIdWithDocumentsAndCompany(sessionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> useCase.getDetail(companyId, sessionId))
            .isInstanceOf(ConsultingSessionException.class)
            .hasMessage(ErrorCode.NOT_FOUND_CONSULTING_SESSION.getMessage());
    }

    @DisplayName("컨설팅 세션 상세 조회 실패 - 회사 ID 불일치")
    @Test
    void getDetailFailInvalidCompany() {
        // given
        given(consultingSessionRepository.findByIdWithDocumentsAndCompany(sessionId))
            .willReturn(Optional.of(consultingSession));

        // when & then
        assertThatThrownBy(() -> useCase.getDetail(differentCompanyId, sessionId))
            .isInstanceOf(ConsultingSessionException.class)
            .hasMessage(ErrorCode.INVALID_COMPANY.getMessage());
    }



    @DisplayName("컨설팅 세션 추가 성공")
    @Test
    void addConsultingSessionSuccess() {
        // given
        given(companyRepository.findById(companyId)).willReturn(Optional.of(consultingSession.getCompany()));
        given(consultingSessionRepository.save(any())).willReturn(consultingSession);

        // when
        ConsultingSessionResponse response = useCase.add(companyId, sessionId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(sessionId);
    }

    @DisplayName("컨설팅 세션 추가 실패 - 회사 없음")
    @Test
    void addConsultingSessionFailCompanyNotFound() {
        // given
        given(companyRepository.findById(companyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> useCase.add(companyId, sessionId))
            .isInstanceOf(ConsultingSessionException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @DisplayName("참여자 검증 성공")
    @Test
    void validateAccountIsParticipantSuccess() {
        // given
        consultingSession.addParticipantAccountId(sessionId);
        given(consultingSessionRepository.findByIdWithParticipants(sessionId)).willReturn(Optional.of(consultingSession));

        // when
        useCase.validateAccountIsParticipant(sessionId, sessionId);

        // then
        verify(consultingSessionRepository).findByIdWithParticipants(sessionId);
    }

    @DisplayName("참여자 검증 실패 - 참여자 아님")
    @Test
    void validateAccountIsParticipantFailNotParticipant() {
        // given
        given(consultingSessionRepository.findByIdWithParticipants(sessionId)).willReturn(Optional.of(consultingSession));

        // when & then
        assertThatThrownBy(() -> useCase.validateAccountIsParticipant(sessionId, 3L))
            .isInstanceOf(ConsultingSessionException.class)
            .hasMessage(ErrorCode.CONSULTING_ACCESS_DENIED.getMessage());
    }

}
