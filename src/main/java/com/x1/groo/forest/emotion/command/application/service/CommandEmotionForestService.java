package com.x1.groo.forest.emotion.command.application.service;

public interface CommandEmotionForestService {
    void unplaceItemById(int userId, int placementId);

    void unplaceAllItems(int userId, int forestId);
}
