package com.x1.groo.forest.emotion.command.application.service;

public interface CommandEmotionForestService {
    void retrieveItemById(int userId, int placementId);

    void retrieveAllItems(int userId, int forestId);
}
