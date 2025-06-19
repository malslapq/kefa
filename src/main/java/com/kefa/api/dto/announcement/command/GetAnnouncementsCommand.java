package com.kefa.api.dto.announcement.command;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAnnouncementsCommand {

    private String keyword;
    private String searchType;
    private int page;
    private int size;

}
