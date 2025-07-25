package com.kefa.api.dto.contact.response;

import com.kefa.common.type.ContactStatus;
import com.kefa.domain.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ContactDetailResponse {

    private Long id;
    private String name;
    private String businessNumber;
    private String email;
    private String phoneNumber;
    private String content;
    private ContactStatus status;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;


    /**
     * Creates a ContactDetailResponse by mapping fields from the given Contact entity.
     *
     * @param contact the Contact entity to convert
     * @return a ContactDetailResponse containing detailed information from the provided Contact
     */
    public static ContactDetailResponse from(Contact contact) {
        return ContactDetailResponse.builder()
            .id(contact.getId())
            .name(contact.getName())
            .businessNumber(contact.getBusinessNumber())
            .email(contact.getEmail())
            .phoneNumber(contact.getPhoneNumber())
            .content(contact.getContent())
            .status(contact.getStatus())
            .resolvedAt(contact.getResolvedAt())
            .createdAt(contact.getCreatedAt())
            .build();
    }
}
