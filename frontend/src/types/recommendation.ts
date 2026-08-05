export interface RecommendationResponse {
  id: string;
  title: string;
  category: string;
  severity: string;
  riskScore: number;
  description: string;
  affectedModule: string;
  recommendedAction: string;
  createdAt: string;
}
