package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.notice.command.NoticesGetCommand;
import com.kefa.api.dto.notice.request.NoticeCreateRequestDto;
import com.kefa.api.dto.notice.request.NoticeUpdateRequestDto;
import com.kefa.api.dto.notice.response.NoticeCreateResponseDto;
import com.kefa.api.dto.notice.response.NoticeResponseDto;
import com.kefa.api.dto.notice.response.NoticeUpdateResponseDto;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NoticeException;
import com.kefa.common.type.NoticesSearchType;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Notice;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.NoticeRepository;
import com.kefa.infrastructure.security.auth.LoginAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoticeUseCaseTest {

    @InjectMocks
    private NoticeUseCase noticeUseCase;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private AccountRepository accountRepository;

    private Account testAccount;
    private LoginAccount loginAccount;
    private Notice testNotice;

    private final int page = 0;
    private final int size = 10;
    private final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
            .id(1L)
            .email("test@example.com")
            .name("테스트유저")
            .build();

        loginAccount = LoginAccount.builder()
            .id(testAccount.getId())
            .name(testAccount.getName())
            .role(null) // 또는 적절한 Role 값
            .jwtId(null)
            .build();

        testNotice = Notice.builder()
            .id(1L)
            .title("테스트 공지사항")
            .content("테스트 내용입니다.")
            .account(testAccount)
            .viewCount(0)
            .isPinned(false)
            .build();
    }

    @DisplayName("공지사항 생성 - 성공")
    @Test
    void createNotice_success() {
        // given
        NoticeCreateRequestDto requestDto = new NoticeCreateRequestDto("새 공지사항", "새로운 내용", false);
        given(accountRepository.findById(any(Long.class))).willReturn(Optional.of(testAccount));
        given(noticeRepository.save(any(Notice.class))).willReturn(testNotice);

        // when
        NoticeCreateResponseDto response = noticeUseCase.create(requestDto, loginAccount);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트 공지사항");
        assertThat(response.getContent()).isEqualTo("테스트 내용입니다.");
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @DisplayName("공지사항 수정 - 성공")
    @Test
    void updateNotice_success() {
        // given
        NoticeUpdateRequestDto requestDto = new NoticeUpdateRequestDto("수정된 제목", "수정된 내용", true);
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.of(testNotice));

        // when
        NoticeUpdateResponseDto response = noticeUseCase.update(requestDto, testNotice.getId(), loginAccount);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(response.isPinned()).isTrue();
    }

    @DisplayName("공지사항 수정 실패 - 권한 없음")
    @Test
    void updateNotice_forbidden() {
        // given
        NoticeUpdateRequestDto requestDto = new NoticeUpdateRequestDto("수정된 제목", "수정된 내용", true);
        Account anotherAccount = Account.builder().id(2L).email("another@example.com").name("다른유저").build();
        LoginAccount anotherLoginAccount = LoginAccount.builder()
            .id(anotherAccount.getId())
            .name(anotherAccount.getName())
            .role(null) // 또는 적절한 Role 값
            .jwtId(null)
            .build();
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.of(testNotice));

        // when & then
        assertThatThrownBy(() -> noticeUseCase.update(requestDto, testNotice.getId(), anotherLoginAccount))
            .isInstanceOf(NoticeException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @DisplayName("공지사항 삭제 - 성공")
    @Test
    void deleteNotice_success() {
        // given
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.of(testNotice));

        // when
        noticeUseCase.delete(testNotice.getId(), loginAccount);

        // then
        verify(noticeRepository, times(1)).delete(any(Notice.class));
    }

    @DisplayName("공지사항 삭제 실패 - 권한 없음")
    @Test
    void deleteNotice_forbidden() {
        // given
        Account anotherAccount = Account.builder().id(2L).email("another@example.com").name("다른유저").build();
        LoginAccount anotherLoginAccount = LoginAccount.builder()
            .id(anotherAccount.getId())
            .name(anotherAccount.getName())
            .role(null) // 또는 적절한 Role 값
            .jwtId(null)
            .build();
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.of(testNotice));

        // when & then
        assertThatThrownBy(() -> noticeUseCase.delete(testNotice.getId(), anotherLoginAccount))
            .isInstanceOf(NoticeException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @DisplayName("단일 공지사항 조회 - 성공")
    @Test
    void getNotice_success() {
        // given
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.of(testNotice));

        // when
        NoticeResponseDto response = noticeUseCase.getNotice(testNotice.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("테스트 공지사항");
        assertThat(response.getContent()).isEqualTo("테스트 내용입니다.");
        assertThat(response.getWriter()).isEqualTo("테스트유저");
    }

    @DisplayName("단일 공지사항 조회 - 공지사항 없음")
    @Test
    void getNotice_notFound() {
        // given
        given(noticeRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeUseCase.getNotice(999L))
            .isInstanceOf(NoticeException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_NOTICE);
    }

    @DisplayName("검색 조건 없는 전체 공지사항 조회 - 성공")
    @Test
    void getNotices_noSearchCondition_success() {
        // given
        NoticesGetCommand command = NoticesGetCommand.builder().page(page).size(size).build();
        Page<Notice> pageResult = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findAll(pageable)).willReturn(pageResult);

        // when
        PagedResponse<NoticeResponseDto> response = noticeUseCase.getNotices(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 공지사항");
    }

    @DisplayName("제목으로 공지사항 검색 - 성공")
    @Test
    void getNotices_byTitle_success() {
        // given
        NoticesGetCommand command = NoticesGetCommand.builder()
            .keyword("테스트")
            .searchType(NoticesSearchType.TITLE.getType())
            .page(page)
            .size(size)
            .build();
        Page<Notice> pageResult = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByTitleContaining(command.getKeyword(), pageable)).willReturn(pageResult);

        // when
        PagedResponse<NoticeResponseDto> response = noticeUseCase.getNotices(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 공지사항");
    }

    @DisplayName("내용으로 공지사항 검색 - 성공")
    @Test
    void getNotices_byContent_success() {
        // given
        NoticesGetCommand command = NoticesGetCommand.builder()
            .keyword("내용")
            .searchType(NoticesSearchType.CONTENT.getType())
            .page(page)
            .size(size)
            .build();
        Page<Notice> pageResult = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByContentContaining(command.getKeyword(), pageable)).willReturn(pageResult);

        // when
        PagedResponse<NoticeResponseDto> response = noticeUseCase.getNotices(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getContent()).isEqualTo("테스트 내용입니다.");
    }

    @DisplayName("작성자로 공지사항 검색 - 성공")
    @Test
    void getNotices_byWriter_success() {
        // given
        NoticesGetCommand command = NoticesGetCommand.builder()
            .keyword("테스트유저")
            .searchType(NoticesSearchType.WRITER.getType())
            .page(page)
            .size(size)
            .build();
        Page<Notice> pageResult = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByAccount_NameContaining(command.getKeyword(), pageable)).willReturn(pageResult);

        // when
        PagedResponse<NoticeResponseDto> response = noticeUseCase.getNotices(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getWriter()).isEqualTo("테스트유저");
    }

    @DisplayName("검색어는 있으나 검색 타입이 잘못된 경우 전체 검색")
    @Test
    void getNotices_withInvalidSearchType() {
        // given
        NoticesGetCommand command = NoticesGetCommand.builder()
            .keyword("아무거나")
            .searchType("잘못된타입")
            .page(page)
            .size(size)
            .build();
        Page<Notice> pageResult = new PageImpl<>(Collections.singletonList(testNotice));

        given(noticeRepository.findAll(pageable)).willReturn(pageResult);

        // when
        PagedResponse<NoticeResponseDto> response = noticeUseCase.getNotices(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }
}
