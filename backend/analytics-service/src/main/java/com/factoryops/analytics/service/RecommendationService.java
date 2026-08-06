package com.factoryops.analytics.service;

import com.factoryops.analytics.dto.response.RecommendationResponse;
import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> getRecommendations();
}
