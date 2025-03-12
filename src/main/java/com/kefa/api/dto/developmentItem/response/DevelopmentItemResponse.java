package com.kefa.api.dto.developmentItem.response;

import com.kefa.domain.entity.DevelopmentItem;
import com.kefa.domain.type.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DevelopmentItemResponse {

    private Long id;
    private String name;
    private Stage stage;
    private String technologyField;
    private String description;

    public static DevelopmentItemResponse from(DevelopmentItem developmentItem) {
        return DevelopmentItemResponse.builder()
            .id(developmentItem.getId())
            .name(developmentItem.getName())
            .stage(developmentItem.getStage())
            .technologyField(developmentItem.getTechnologyField())
            .description(developmentItem.getDescription())
            .build();
    }

}
