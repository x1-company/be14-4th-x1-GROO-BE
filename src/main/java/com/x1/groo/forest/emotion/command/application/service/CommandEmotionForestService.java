package com.x1.groo.forest.emotion.command.application.service;

import com.x1.groo.forest.emotion.command.domain.vo.RequestPlacementVO;

public interface CommandEmotionForestService {
    void retrieveItemById(int userId, int placementId);

    void retrieveAllItems(int userId, int forestId);

    void placeItem(int userId, RequestPlacementVO requestPlacementVO);
}
