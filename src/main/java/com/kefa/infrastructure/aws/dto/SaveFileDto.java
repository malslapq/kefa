package com.kefa.infrastructure.aws.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class SaveFileDto {

    private String url;
    private String name;

}
