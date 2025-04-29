package com.x1.groo.forest.emotion.query.service;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionDetailDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxListDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import com.x1.groo.forest.emotion.query.repository.QueryForestEmotionMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryForestEmotionServiceImpl implements QueryForestEmotionService {

    @Autowired
    private QueryForestEmotionMapper queryForestEmotionMapper;

    // 사용자가 보유한 기억의 조각 카테고리별 조회
    public List<QueryForestEmotionUserItemDTO> getPieceOfMemory (int userId, int categoryId) {
        return queryForestEmotionMapper.findPieceOfMemory(userId, categoryId);
    }

    public List<QueryForestEmotionMailboxListDTO> getMailboxList (int forestId) {
        return queryForestEmotionMapper.findMailboxList(forestId);
    }

    public List<QueryForestEmotionMailboxDTO> getMailboxDetail (int id) {
        return queryForestEmotionMapper.findMailboxDetail(id);
    }

    public List<QueryForestEmotionDetailDTO> getForestDetail(int forestId) {
        return queryForestEmotionMapper.findForestDetail(forestId);
    }

}
