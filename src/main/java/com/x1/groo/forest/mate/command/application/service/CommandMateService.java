package com.x1.groo.forest.mate.command.application.service;

public interface CommandMateService {
    String createInviteLink(int forestId);

    void acceptInvite(int userId, String inviteCode);

    void quit(int userId, int forestId);
}
