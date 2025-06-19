package com.kefa.api.dto.account.command;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAccountsCommand {

    private String keyword;
    private String searchType;
    private int page;
    private int size;

}
