package com.kefa.api.controller;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.contact.command.ContactGetAllCommand;
import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.api.dto.contact.request.ContactUpdateStatusRequest;
import com.kefa.api.dto.contact.response.ContactDetailResponse;
import com.kefa.application.service.ContactService;
import com.kefa.common.response.ApiResponse;
import com.kefa.common.type.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * Creates a new contact using the provided request data.
     *
     * @param request the contact information to be created
     * @return an ApiResponse containing the details of the newly created contact
     */
    @PostMapping("/contact")
    public ApiResponse<ContactDetailResponse> addContact(@RequestBody @Valid ContactRequest request) {
        return ApiResponse.success(contactService.add(request));
    }

    /**
     * Retrieves a paginated list of contacts, optionally filtered by contact status.
     *
     * @param page   the page number to retrieve (default is 0)
     * @param size   the number of contacts per page (default is 10)
     * @param status optional filter for contact status
     * @return a successful response containing the paginated list of contact details
     */
    @GetMapping("/contacts")
    public ApiResponse<PagedResponse<ContactDetailResponse>> getAllContacts(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(required = false) ContactStatus status) {

        ContactGetAllCommand command = ContactGetAllCommand.builder()
            .page(page)
            .size(size)
            .status(status)
            .build();

        return ApiResponse.success(contactService.getAll(command));
    }

    /**
     * Retrieves detailed information for a specific contact by its ID.
     *
     * Only users with roles ADMIN, EXPERT, or STAFF are authorized to access this endpoint.
     *
     * @param contactId the unique identifier of the contact to retrieve
     * @return an ApiResponse containing the contact's detailed information
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @GetMapping("/contacts/{contactId}")
    public ApiResponse<ContactDetailResponse> getContactById(@PathVariable Long contactId) {
        return ApiResponse.success(contactService.getDetail(contactId));
    }

    /**
     * Updates the status of a specific contact.
     *
     * Only users with ADMIN, EXPERT, or STAFF roles are authorized to perform this operation.
     *
     * @param contactId the ID of the contact to update
     * @param request the request containing the new status information
     * @return an ApiResponse containing the updated contact details
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @PatchMapping("/contacts/{contactId}/status")
    public ApiResponse<ContactDetailResponse> updateStatus(@PathVariable Long contactId, @RequestBody ContactUpdateStatusRequest request) {
        return ApiResponse.success(contactService.updateStatus(contactId, request));
    }

    /**
     * Deletes a contact by its ID.
     *
     * Only users with ADMIN, EXPERT, or STAFF roles are authorized to perform this operation.
     *
     * @param contactId the unique identifier of the contact to delete
     * @return a successful ApiResponse with no content if the contact is deleted
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @DeleteMapping("/contacts/{contactId}")
    public ApiResponse<Void> deleteContact(@PathVariable Long contactId) {
        return ApiResponse.success(contactService.delete(contactId));
    }

}
