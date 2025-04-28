package com.x1.groo.forest.emotion.query.service;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import java.util.List;

public interface QueryForestEmotionService {
    List<QueryForestEmotionUserItemDTO> getPieceOfMemory(int userId, int categoryId);

    List<QueryForestEmotionMailboxDTO> getMailboxList(int userId, int forestId);

    List<QueryForestEmotionMailboxDTO> getMailboxDetail(int userId, int id);
}

