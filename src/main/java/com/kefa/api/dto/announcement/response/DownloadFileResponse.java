package com.kefa.api.dto.announcement.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DownloadFileResponse {

    private Long id;
    private String name;
    private String url;

}
