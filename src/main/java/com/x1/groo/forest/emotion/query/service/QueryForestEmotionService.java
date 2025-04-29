package com.x1.groo.forest.emotion.query.service;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionDetailDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxListDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import java.util.List;

public interface QueryForestEmotionService {
    List<QueryForestEmotionUserItemDTO> getPieceOfMemory(int userId, int categoryId);

    List<QueryForestEmotionMailboxListDTO> getMailboxList(int forestId);

    List<QueryForestEmotionMailboxDTO> getMailboxDetail(int id);

    List<QueryForestEmotionDetailDTO> getForestDetail(int forestId);
}

