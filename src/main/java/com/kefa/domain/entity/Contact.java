package com.kefa.domain.entity;

import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.common.type.ContactStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE contacts SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "contacts")
@Entity
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String businessNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactStatus status;

    private LocalDateTime resolvedAt;

    /**
     * Creates a new Contact entity from the provided ContactRequest, initializing its status to PENDING.
     *
     * @param request the data transfer object containing contact details
     * @return a new Contact instance populated with data from the request
     */
    public static Contact from(ContactRequest request) {
        return Contact.builder()
            .name(request.getName())
            .businessNumber(request.getBusinessNumber())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .content(request.getContent())
            .status(ContactStatus.PENDING)
            .build();
    }

    /**
     * Updates the status of this contact to the specified value.
     *
     * @param status the new status to set for the contact
     */
    public void updateStatus(ContactStatus status) {
        this.status = status;
    }
}
