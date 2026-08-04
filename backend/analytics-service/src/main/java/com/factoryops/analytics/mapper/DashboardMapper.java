package com.factoryops.analytics.mapper;


import com.factoryops.analytics.dto.response.DashboardResponse;
import com.factoryops.analytics.entity.Dashboard;

public interface DashboardMapper {


    DashboardResponse toResponse(Dashboard dashboard);

}