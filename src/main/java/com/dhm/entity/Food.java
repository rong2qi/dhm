package com.dhm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 食物实体类
 */
@Data
@TableName("food")
public class Food {
    @TableId(value = "food_id", type = IdType.AUTO)
    private Integer foodId;
    
    @TableField("name")
    private String name;
    
    @TableField("description")
    private String description;
    
    @TableField("serving_size")
    private String servingSize;
    
    @TableField("calories")
    private Double calories;
    
    @TableField("protein")
    private Double protein;
    
    @TableField("fat")
    private Double fat;
    
    @TableField("carbs")
    private Double carbs;
    
    @TableField("category")
    private String category;
} 