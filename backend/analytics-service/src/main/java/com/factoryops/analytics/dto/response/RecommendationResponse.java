package com.factoryops.analytics.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    
    private String id;
    
    private String title;
    
    private String category;
    
    private String severity;
    
    private Integer riskScore;
    
    private String description;
    
    private String affectedModule;
    
    private String recommendedAction;
    
    private LocalDateTime createdAt;
}
