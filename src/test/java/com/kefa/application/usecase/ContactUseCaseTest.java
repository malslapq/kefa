package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.contact.command.ContactGetAllCommand;
import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.api.dto.contact.request.ContactUpdateStatusRequest;
import com.kefa.api.dto.contact.response.ContactDetailResponse;
import com.kefa.common.exception.ContactException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.type.ContactStatus;
import com.kefa.domain.entity.Contact;
import com.kefa.infrastructure.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactUseCaseTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactUseCase contactUseCase;

    @Test
    @DisplayName("상담 문의 등록 - 성공")
    void addContact_Success() {
        ContactRequest request = ContactRequest.builder()
            .name("name")
            .businessNumber("12-123-12345")
            .email("test@test.com")
            .phoneNumber("01012344321")
            .content("나문희")
            .build();

        Contact contact = Contact.from(request);
        given(contactRepository.save(any())).willReturn(contact);

        ContactDetailResponse response = contactUseCase.add(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getBusinessNumber()).isEqualTo("12-123-12345");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getPhoneNumber()).isEqualTo("01012344321");
        assertThat(response.getContent()).isEqualTo("나문희");
    }

    @Test
    @DisplayName("상담 문의 상태 변경 - 성공")
    void updateStatus_Success() {
        Long contactId = 1L;
        ContactUpdateStatusRequest request = new ContactUpdateStatusRequest(ContactStatus.RESOLVED);
        Contact contact = Contact.builder().id(contactId).status(ContactStatus.PENDING).build();

        given(contactRepository.findById(contactId)).willReturn(Optional.of(contact));
        given(contactRepository.save(any(Contact.class))).willAnswer(invocation -> invocation.getArgument(0));

        ContactDetailResponse response = contactUseCase.updateStatus(contactId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(request.getStatus());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    @DisplayName("상담 문의 상태 변경 - 실패 상담 문의를 찾을 수 없음")
    void updateStatus_Fail_NotFound() {
        Long contactId = 1L;
        ContactUpdateStatusRequest request = new ContactUpdateStatusRequest(ContactStatus.RESOLVED);
        given(contactRepository.findById(contactId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contactUseCase.updateStatus(contactId, request))
            .isInstanceOf(ContactException.class)
            .hasMessage(ErrorCode.NOT_FOUND_CONTACT.getMessage());
    }

    @Test
    @DisplayName("상담 문의 삭제 - 성공")
    void deleteContact_Success() {
        Long contactId = 1L;

        contactUseCase.delete(contactId);

        verify(contactRepository, times(1)).deleteById(contactId);
    }

    @Test
    @DisplayName("상담 문의 조회 - 성공 상태 필터링")
    void getAll_WithStatus_Success() {
        ContactGetAllCommand command = new ContactGetAllCommand(0, 10, ContactStatus.PENDING);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Contact> contacts = List.of(Contact.builder().build());
        Page<Contact> page = new PageImpl<>(contacts, pageable, contacts.size());

        given(contactRepository.findByStatus(command.getStatus(), pageable)).willReturn(page);

        PagedResponse<ContactDetailResponse> response = contactUseCase.getAll(command);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("상담 문의 전체 조회 - 성공 필터링 없음")
    void getAll_NoStatus_Success() {
        ContactGetAllCommand command = new ContactGetAllCommand(0, 10, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Contact> contacts = List.of(Contact.builder().build());
        Page<Contact> page = new PageImpl<>(contacts, pageable, contacts.size());

        given(contactRepository.findAll(pageable)).willReturn(page);

        PagedResponse<ContactDetailResponse> response = contactUseCase.getAll(command);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}
