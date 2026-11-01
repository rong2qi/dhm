package com.dhm.service;

import com.dhm.entity.Plan;
import com.dhm.entity.User;

import java.util.List;

public interface DietPlanService {
    
    /**
     * 生成饮食建议
     * @param user 用户对象
     * @return 生成的饮食计划
     */
    Plan generateDietPlan(User user);
    
    /**
     * 根据ID获取饮食计划
     * @param planId 计划ID
     * @return 饮食计划详情
     */
    Plan getPlanById(Integer planId);
    
    /**
     * 获取用户所有饮食计划（包括历史记录和当前计划）
     * @param userId 用户ID
     * @return 饮食计划列表
     */
    List<Plan> getUserPlans(Integer userId);
    
    /**
     * 获取用户的历史记录（仅日志类型）
     * @param userId 用户ID
     * @return 历史记录列表
     */
    List<Plan> getUserHistory(Integer userId);
    
    /**
     * 将计划转为日志（将计划复制为日志记录）
     * @param planId 计划ID
     * @return 新创建的日志ID
     */
    Integer convertPlanToLog(Integer planId);
    
    /**
     * 保存计划或日志
     * @param plan 计划对象
     * @return 是否保存成功
     */
    boolean savePlan(Plan plan);
    
    /**
     * 删除计划或日志
     * @param planId 计划ID
     * @return 是否删除成功
     */
    boolean deletePlan(Integer planId);
} 