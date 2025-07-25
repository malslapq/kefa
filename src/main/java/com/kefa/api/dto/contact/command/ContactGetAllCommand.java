package com.kefa.api.dto.contact.command;

import com.kefa.common.type.ContactStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactGetAllCommand {

    private int page;
    private int size;
    private ContactStatus status;

}
