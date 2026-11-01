package com.dhm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhm.entity.Food;
import com.dhm.entity.Meal;
import com.dhm.entity.MealFood;
import com.dhm.entity.Plan;
import com.dhm.entity.User;
import com.dhm.mapper.FoodMapper;
import com.dhm.mapper.MealFoodMapper;
import com.dhm.mapper.MealMapper;
import com.dhm.mapper.PlanMapper;
import com.dhm.service.DietPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class DietPlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements DietPlanService {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private MealFoodMapper mealFoodMapper;

    private final Random random = new Random();

    /**
     * 生成饮食建议
     * @param user 用户对象
     * @return 生成的饮食计划
     */
    @Override
    @Transactional
    public Plan generateDietPlan(User user) {
        // 创建新的计划
        Plan plan = new Plan();
        plan.setUserId(user.getUserId());

        // 使用当前日期，格式化为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        plan.setRecordDate(dateFormat.format(new Date()));

        plan.setRecordType("计划");

        // 根据健康目标设置计划名称
        String goalPrefix = "";
        switch (user.getHealthGoal()) {
            case "减重":
                goalPrefix = "减重";
                break;
            case "增肌":
                goalPrefix = "增肌";
                break;
            default:
                goalPrefix = "均衡";
                break;
        }
        plan.setPlanName("系统推荐" + goalPrefix + "饮食计划");

        // 创建餐次列表
        List<Meal> meals = new ArrayList<>();

        // 生成四个餐次：早餐、午餐、晚餐、加餐
        String[] mealTypes = {"早餐", "午餐", "晚餐", "加餐"};
        for (String mealType : mealTypes) {
            Meal meal = new Meal();
            meal.setMealType(mealType);

            // 为每个餐次生成食物
            List<MealFood> mealFoods = generateFoodsForMeal(mealType, user.getHealthGoal());
            meal.setMealFoods(mealFoods);

            meals.add(meal);
        }

        plan.setMeals(meals);

        // 计算总的营养成分
        calculateTotalNutrition(plan);

        // 保存计划到数据库
        savePlanWithDetails(plan);

        return plan;
    }

    /**
     * 为餐次生成食物
     * @param mealType 餐次类型
     * @param healthGoal 健康目标
     * @return 食物列表
     */
    private List<MealFood> generateFoodsForMeal(String mealType, String healthGoal) {
        List<MealFood> mealFoods = new ArrayList<>();

        // 根据餐次和健康目标定义食物组合模板
        Map<String, Integer> foodCountMap = new HashMap<>();

        // 根据餐次定义基本食物组合模板
        switch (mealType) {
            case "早餐":
                foodCountMap.put("主食", 1);
                foodCountMap.put("肉类", 1);
                foodCountMap.put("乳制品", 1);
                break;
            case "午餐":
                foodCountMap.put("主食", 1);
                foodCountMap.put("肉类", 1);
                foodCountMap.put("蔬菜", 2);
                break;
            case "晚餐":
                foodCountMap.put("主食", 1);
                foodCountMap.put("肉类", 1);
                foodCountMap.put("蔬菜", 1);
                break;
            case "加餐":
                foodCountMap.put("水果", 1);
                foodCountMap.put("乳制品", 1);
                break;
        }

        // 根据健康目标调整食物组合
        switch (healthGoal) {
            case "减重":
                // 减少主食，增加蔬菜和水果
                if (foodCountMap.containsKey("主食")) {
                    foodCountMap.put("主食", Math.max(1, foodCountMap.get("主食") - 1));
                }
                if (foodCountMap.containsKey("蔬菜")) {
                    foodCountMap.put("蔬菜", foodCountMap.get("蔬菜") + 1);
                } else {
                    foodCountMap.put("蔬菜", 1);
                }
                break;
            case "增肌":
                // 增加主食和肉类
                if (foodCountMap.containsKey("主食")) {
                    foodCountMap.put("主食", foodCountMap.get("主食") + 1);
                }
                if (foodCountMap.containsKey("肉类")) {
                    foodCountMap.put("肉类", foodCountMap.get("肉类") + 1);
                }
                if (foodCountMap.containsKey("乳制品")) {
                    foodCountMap.put("乳制品", foodCountMap.get("乳制品") + 1);
                }
                break;
            default:
                // 维持身材保持均衡
                break;
        }

        // 根据模板随机选择食物
        for (Map.Entry<String, Integer> entry : foodCountMap.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue();

            // 随机选择指定数量的食物
            List<Food> selectedFoods = getRandomFoodsByCategory(category, count);

            // 将选中的食物添加到餐次中
            for (Food food : selectedFoods) {
                MealFood mealFood = new MealFood();
                mealFood.setFoodId(food.getFoodId());
                mealFood.setServingAmount(1.0); // 默认份数为1

                // 设置营养成分
                mealFood.setCalculatedCalories(food.getCalories() * mealFood.getServingAmount());
                mealFood.setCalculatedProtein(food.getProtein() * mealFood.getServingAmount());
                mealFood.setCalculatedFat(food.getFat() * mealFood.getServingAmount());
                mealFood.setCalculatedCarbs(food.getCarbs() * mealFood.getServingAmount());

                // 设置关联的食物对象
                mealFood.setFood(food);

                mealFoods.add(mealFood);
            }
        }

        return mealFoods;
    }

    /**
     * 随机获取指定类别的食物
     * @param category 食物类别
     * @param count 数量
     * @return 食物列表
     */
    private List<Food> getRandomFoodsByCategory(String category, int count) {
        // 查询指定类别的所有食物
        LambdaQueryWrapper<Food> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Food::getCategory, category);
        List<Food> foods = foodMapper.selectList(queryWrapper);

        // 随机选择指定数量的食物
        List<Food> selectedFoods = new ArrayList<>();

        if (foods != null && !foods.isEmpty()) {
            for (int i = 0; i < count; i++) {
                if (foods.size() > 0) {
                    int randomIndex = random.nextInt(foods.size());
                    selectedFoods.add(foods.get(randomIndex));
                    foods.remove(randomIndex); // 避免重复选择
                }
            }
        }

        return selectedFoods;
    }

    /**
     * 计算总营养成分
     * @param plan 饮食计划
     */
    private void calculateTotalNutrition(Plan plan) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        // 遍历所有餐次和食物，累加营养成分
        for (Meal meal : plan.getMeals()) {
            for (MealFood mealFood : meal.getMealFoods()) {
                totalCalories += mealFood.getCalculatedCalories();
                totalProtein += mealFood.getCalculatedProtein();
                totalFat += mealFood.getCalculatedFat();
                totalCarbs += mealFood.getCalculatedCarbs();
            }
        }

        // 设置总营养成分
        plan.setTotalCalories(totalCalories);
        plan.setTotalProtein(totalProtein);
        plan.setTotalFat(totalFat);
        plan.setTotalCarbs(totalCarbs);
    }

    /**
     * 保存饮食计划到数据库
     * @param plan 饮食计划
     */
    private void savePlanWithDetails(Plan plan) {
        // 1. 保存计划
        this.save(plan);

        // 2. 保存餐次
        for (Meal meal : plan.getMeals()) {
            meal.setRecordId(plan.getRecordId());
            mealMapper.insert(meal);

            // 3. 保存餐次-食物关联
            for (MealFood mealFood : meal.getMealFoods()) {
                mealFood.setMealId(meal.getMealId());
                mealFoodMapper.insert(mealFood);
            }
        }
    }

    /**
     * 根据ID获取饮食计划
     * @param planId 计划ID
     * @return 饮食计划详情
     */
    @Override
    public Plan getPlanById(Integer planId) {
        // 1. 获取计划基本信息
        Plan plan = this.getById(planId);
        if (plan == null) {
            return null;
        }

        // 2. 获取餐次信息
        LambdaQueryWrapper<Meal> mealQueryWrapper = new LambdaQueryWrapper<>();
        mealQueryWrapper.eq(Meal::getRecordId, planId);
        List<Meal> meals = mealMapper.selectList(mealQueryWrapper);

        // 3. 获取每个餐次的食物
        for (Meal meal : meals) {
            LambdaQueryWrapper<MealFood> mealFoodQueryWrapper = new LambdaQueryWrapper<>();
            mealFoodQueryWrapper.eq(MealFood::getMealId, meal.getMealId());
            List<MealFood> mealFoods = mealFoodMapper.selectList(mealFoodQueryWrapper);

            // 获取每个食物的详细信息
            for (MealFood mealFood : mealFoods) {
                Food food = foodMapper.selectById(mealFood.getFoodId());
                mealFood.setFood(food);
            }

            meal.setMealFoods(mealFoods);
        }

        plan.setMeals(meals);

        return plan;
    }

    /**
     * 获取用户所有饮食计划（包括历史记录和当前计划）
     * @param userId 用户ID
     * @return 饮食计划列表
     */
    @Override
    public List<Plan> getUserPlans(Integer userId) {
        LambdaQueryWrapper<Plan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Plan::getUserId, userId);
        queryWrapper.eq(Plan::getRecordType, "计划");
        queryWrapper.orderByDesc(Plan::getRecordDate);
        return this.list(queryWrapper);
    }

    /**
     * 获取用户的历史记录（仅日志类型）
     * @param userId 用户ID
     * @return 历史记录列表
     */
    @Override
    public List<Plan> getUserHistory(Integer userId) {
        LambdaQueryWrapper<Plan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Plan::getUserId, userId);
        queryWrapper.eq(Plan::getRecordType, "日志");
        queryWrapper.orderByDesc(Plan::getRecordDate);
        return this.list(queryWrapper);
    }

    /**
     * 将计划转为日志（将计划复制为日志记录，并删除原计划）
     * @param planId 计划ID
     * @return 新创建的日志ID
     */
    @Override
    @Transactional
    public Integer convertPlanToLog(Integer planId) {
        // 1. 获取原计划
        Plan oldPlan = this.getPlanById(planId);
        if (oldPlan == null || !"计划".equals(oldPlan.getRecordType())) {
            return null;
        }

        // 2. 创建新计划作为日志
        Plan newPlan = new Plan();
        newPlan.setUserId(oldPlan.getUserId());

        // 使用当前日期，格式化为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        newPlan.setRecordDate(dateFormat.format(new Date()));

        newPlan.setRecordType("日志");
        newPlan.setPlanName(oldPlan.getPlanName() + " - 记录");
        newPlan.setTotalCalories(oldPlan.getTotalCalories());
        newPlan.setTotalProtein(oldPlan.getTotalProtein());
        newPlan.setTotalFat(oldPlan.getTotalFat());
        newPlan.setTotalCarbs(oldPlan.getTotalCarbs());

        // 3. 保存新计划
        this.save(newPlan);

        // 4. 复制餐次和食物
        for (Meal oldMeal : oldPlan.getMeals()) {
            // 创建新餐次
            Meal newMeal = new Meal();
            newMeal.setRecordId(newPlan.getRecordId());
            newMeal.setMealType(oldMeal.getMealType());

            // 设置当前时间，格式化为字符串
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            newMeal.setMealTime(timeFormat.format(new Date()));

            // 保存新餐次
            mealMapper.insert(newMeal);

            // 复制食物
            for (MealFood oldMealFood : oldMeal.getMealFoods()) {
                MealFood newMealFood = new MealFood();
                newMealFood.setMealId(newMeal.getMealId());
                newMealFood.setFoodId(oldMealFood.getFoodId());
                newMealFood.setServingAmount(oldMealFood.getServingAmount());
                newMealFood.setCalculatedCalories(oldMealFood.getCalculatedCalories());
                newMealFood.setCalculatedProtein(oldMealFood.getCalculatedProtein());
                newMealFood.setCalculatedFat(oldMealFood.getCalculatedFat());
                newMealFood.setCalculatedCarbs(oldMealFood.getCalculatedCarbs());

                // 保存新食物关联
                mealFoodMapper.insert(newMealFood);
            }
        }

        // 5. 删除原计划（餐次和食物关联会通过外键级联删除）
        this.removeById(planId);

        return newPlan.getRecordId();
    }

    /**
     * 保存计划或日志
     * @param plan 计划对象
     * @return 是否保存成功
     */
    @Override
    @Transactional
    public boolean savePlan(Plan plan) {
        // 如果是修改，先删除原有餐次和食物
        if (plan.getRecordId() != null) {
            // 删除原有餐次（级联删除会自动删除相关食物）
            LambdaQueryWrapper<Meal> mealQueryWrapper = new LambdaQueryWrapper<>();
            mealQueryWrapper.eq(Meal::getRecordId, plan.getRecordId());
            mealMapper.delete(mealQueryWrapper);
        }

        // 保存计划
        boolean result = this.saveOrUpdate(plan);

        // 保存餐次和食物
        if (result && plan.getMeals() != null) {
            for (Meal meal : plan.getMeals()) {
                meal.setRecordId(plan.getRecordId());
                mealMapper.insert(meal);

                if (meal.getMealFoods() != null) {
                    for (MealFood mealFood : meal.getMealFoods()) {
                        mealFood.setMealId(meal.getMealId());
                        mealFoodMapper.insert(mealFood);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 删除计划或日志
     * @param planId 计划ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deletePlan(Integer planId) {
        // 删除计划（餐次和食物关联会通过外键级联删除）
        return this.removeById(planId);
    }
} 
