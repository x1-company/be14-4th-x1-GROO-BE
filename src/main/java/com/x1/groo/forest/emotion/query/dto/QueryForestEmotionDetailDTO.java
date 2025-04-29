package com.x1.groo.forest.emotion.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueryForestEmotionDetailDTO {

    private int forestId;
    private String name;
    private Boolean isPublic;
    private int backgroundId;
    private int userId;

    private String backgroundImageUrl;

    private int placementId;
    private double placementPositionX;
    private double placementPositionY;

    private int userItemId;
    private int userItemPlacedCount;

    private int itemId;
    private String itemName;
    private String itemImageUrl;

}
