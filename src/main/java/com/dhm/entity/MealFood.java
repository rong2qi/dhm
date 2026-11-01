package com.dhm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 餐次-食物关联实体类
 */
@Data
@TableName("meal_food")
public class MealFood {
    @TableId(value = "meal_food_id", type = IdType.AUTO)
    private Integer mealFoodId;
    
    @TableField("meal_id")
    private Integer mealId;
    
    @TableField("food_id")
    private Integer foodId;
    
    @TableField("serving_amount")
    private Double servingAmount;
    
    @TableField("calculated_calories")
    private Double calculatedCalories;
    
    @TableField("calculated_protein")
    private Double calculatedProtein;
    
    @TableField("calculated_fat")
    private Double calculatedFat;
    
    @TableField("calculated_carbs")
    private Double calculatedCarbs;
    
    @TableField(exist = false)
    private Food food;
} 