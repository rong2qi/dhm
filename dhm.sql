-- 创建数据库
CREATE DATABASE IF NOT EXISTS dhm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE dhm;

-- 用户表
CREATE TABLE `user` (
                        `user_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识符',
                        `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户登录名',
                        `password` CHAR(32) NOT NULL COMMENT '使用MD5加密的密码',
                        `birth_date` VARCHAR(10) COMMENT '用户出生日期',
                        `gender` ENUM('女性','男性','其他') COMMENT '用户性别',
                        `height` DECIMAL(5,2) UNSIGNED NOT NULL COMMENT '用户身高(cm)',
                        `activity_level` ENUM('慵懒','轻度活跃','中等活跃','活跃','非常活跃') NOT NULL DEFAULT '中等活跃' COMMENT '用户活动水平',
                        `health_goal` ENUM('减重','维持身材','增肌') NOT NULL DEFAULT '维持身材' COMMENT '用户健康目标',
                        `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色: user, admin',
                        `dietary_prefs` JSON COMMENT '用户饮食偏好（JSON格式存储）',
                        `created_at` VARCHAR(19) DEFAULT '2025-06-17 00:00:00' COMMENT '用户创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 食物表
CREATE TABLE `food` (
                        `food_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '食物唯一标识符',
                        `name` VARCHAR(100) NOT NULL COMMENT '食物名称',
                        `description` VARCHAR(255) COMMENT '食物描述',
                        `serving_size` VARCHAR(50) NOT NULL COMMENT '食物份量单位（例如：100g, 1个）',
                        `calories` DECIMAL(8,2) UNSIGNED NOT NULL COMMENT '每份食物的卡路里含量',
                        `protein` DECIMAL(8,2) UNSIGNED NOT NULL COMMENT '每份食物的蛋白质含量(g)',
                        `fat` DECIMAL(8,2) UNSIGNED NOT NULL COMMENT '每份食物的脂肪含量(g)',
                        `carbs` DECIMAL(8,2) UNSIGNED NOT NULL COMMENT '每份食物的碳水化合物含量(g)',
                        `category` VARCHAR(50) NOT NULL COMMENT '食物类别（例如：水果、蔬菜、肉类）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 计划/日志表
CREATE TABLE `plan` (
                        `record_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '记录唯一标识符',
                        `user_id` INT NOT NULL COMMENT '关联的用户ID',
                        `record_date` VARCHAR(10) NOT NULL COMMENT '记录日期（计划或日志的日期）',
                        `record_type` ENUM('计划','日志') NOT NULL COMMENT '记录类型：Plan(计划), Log(日志)',
                        `plan_name` VARCHAR(100) COMMENT '计划名称（仅当记录类型为Plan时有效）',
                        `total_calories` DECIMAL(10,2) UNSIGNED COMMENT '该日总卡路里',
                        `total_protein` DECIMAL(10,2) UNSIGNED COMMENT '该日总蛋白质(g)',
                        `total_fat` DECIMAL(10,2) UNSIGNED COMMENT '该日总脂肪(g)',
                        `total_carbs` DECIMAL(10,2) UNSIGNED COMMENT '该日总碳水化合物(g)',
                        `weight` DECIMAL(5,2) UNSIGNED COMMENT '当日体重(kg)',
                        `notes` TEXT COMMENT '备注',
                        FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 餐次表
CREATE TABLE `meal` (
                        `meal_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '餐次唯一标识符',
                        `record_id` INT NOT NULL COMMENT '关联的主记录ID',
                        `meal_type` ENUM('早餐','午餐','晚餐','加餐') NOT NULL COMMENT '餐次类型：早餐、午餐、晚餐、加餐',
                        `meal_time` VARCHAR(8) COMMENT '用餐时间（记录日志时可填写）',
                        `notes` VARCHAR(255) COMMENT '餐次备注',
                        FOREIGN KEY (`record_id`) REFERENCES `plan`(`record_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 餐次-食物关联表
CREATE TABLE `meal_food` (
                             `meal_food_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '餐食明细唯一标识符',
                             `meal_id` INT NOT NULL COMMENT '关联的餐次ID',
                             `food_id` INT NOT NULL COMMENT '关联的食物ID',
                             `serving_amount` DECIMAL(8,2) UNSIGNED NOT NULL COMMENT '食物份数（根据食物表中的份量单位）',
                             `calculated_calories` DECIMAL(10,2) UNSIGNED COMMENT '计算出的卡路里（=食物的卡路里*份数）',
                             `calculated_protein` DECIMAL(10,2) UNSIGNED COMMENT '计算出的蛋白质(g)',
                             `calculated_fat` DECIMAL(10,2) UNSIGNED COMMENT '计算出的脂肪(g)',
                             `calculated_carbs` DECIMAL(10,2) UNSIGNED COMMENT '计算出的碳水化合物(g)',
                             FOREIGN KEY (`meal_id`) REFERENCES `meal`(`meal_id`) ON DELETE CASCADE,
                             FOREIGN KEY (`food_id`) REFERENCES `food`(`food_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引以优化查询性能
CREATE INDEX idx_user_username ON `user`(username);
CREATE INDEX idx_food_name ON `food`(name);
CREATE INDEX idx_plan_user_date ON `plan`(user_id, record_date);
CREATE INDEX idx_meal_record ON `meal`(record_id);
CREATE INDEX idx_meal_food_meal ON `meal_food`(meal_id);
CREATE INDEX idx_meal_food_food ON `meal_food`(food_id);

-- 插入基础食物数据
INSERT INTO food (name, description, serving_size, calories, protein, fat, carbs, category)
VALUES
-- 主食类
('白米饭', '精制大米', '100g', 130, 2.7, 0.3, 28.2, '主食'),
('全麦面包', '全麦粉制作', '1片(35g)', 92, 3.9, 1.2, 16.1, '主食'),
('燕麦片', '即食燕麦', '40g', 148, 5.5, 2.4, 25.6, '主食'),
('意大利面(干)', '未煮制', '100g', 371, 13.0, 1.5, 75.0, '主食'),
('荞麦面', '日式荞麦面', '100g', 114, 4.8, 0.5, 24.8, '主食'),
('玉米饼', '墨西哥式', '1个(50g)', 146, 3.5, 1.8, 29.5, '主食'),
('馒头', '标准大小', '1个(80g)', 210, 6.8, 1.0, 44.0, '主食'),
('小米粥', '稀粥状态', '1碗(250g)', 110, 3.2, 0.8, 22.5, '主食'),
('法棍面包', '传统法式', '50g', 135, 4.8, 0.7, 26.8, '主食'),
('糙米饭', '未精制大米', '100g', 111, 2.6, 0.9, 23.0, '主食'),
('花卷', '标准大小', '1个(70g)', 180, 5.2, 1.2, 35.8, '主食'),
('红薯', '蒸制', '100g', 86, 1.6, 0.1, 20.1, '主食'),
('土豆', '蒸制', '100g', 77, 2.0, 0.1, 17.5, '主食'),
('玉米', '甜玉米粒', '100g', 86, 3.2, 1.2, 19.0, '主食'),
-- 肉类
('鸡胸肉', '水煮', '100g', 165, 31.0, 3.6, 0.0, '肉类'),
('瘦牛肉', '煎制', '100g', 250, 26.0, 15.0, 0.0, '肉类'),
('猪里脊', '烤制', '100g', 143, 20.0, 6.0, 0.0, '肉类'),
('三文鱼', '蒸制', '100g', 208, 20.0, 13.0, 0.0, '肉类'),
('虾仁', '水煮', '100g', 99, 24.0, 0.3, 0.2, '肉类'),
('鸡蛋', '水煮', '1个(50g)', 78, 6.3, 5.3, 0.6, '肉类'),
('鸭胸肉', '烤制', '100g', 133, 19.0, 6.0, 0.0, '肉类'),
('牛排', '菲力部位', '100g', 271, 26.0, 18.0, 0.0, '肉类'),
('鸡腿肉(去皮)', '烤制', '100g', 165, 27.0, 6.0, 0.0, '肉类'),
('鳕鱼', '蒸制', '100g', 82, 18.0, 0.7, 0.0, '肉类'),
-- 蔬菜类
('西兰花', '水煮', '100g', 35, 2.8, 0.4, 7.0, '蔬菜'),
('菠菜', '炒制', '100g', 23, 2.9, 0.4, 3.6, '蔬菜'),
('胡萝卜', '生', '100g', 41, 0.9, 0.2, 10.0, '蔬菜'),
('番茄', '生', '100g', 18, 0.9, 0.2, 3.9, '蔬菜'),
('黄瓜', '生', '100g', 15, 0.7, 0.2, 3.6, '蔬菜'),
('生菜', '生', '100g', 15, 1.4, 0.2, 2.9, '蔬菜'),
('茄子', '蒸制', '100g', 25, 1.0, 0.2, 6.0, '蔬菜'),
('芹菜', '生', '100g', 16, 0.7, 0.2, 3.0, '蔬菜'),
('青椒', '生', '100g', 20, 0.9, 0.2, 4.6, '蔬菜'),
('蘑菇', '鲜香菇', '100g', 22, 2.2, 0.3, 4.0, '蔬菜'),
-- 水果类
('苹果', '红富士', '1个(150g)', 78, 0.3, 0.2, 21.0, '水果'),
('香蕉', '中等大小', '1根(100g)', 89, 1.1, 0.3, 23.0, '水果'),
('橙子', '普通大小', '1个(130g)', 62, 1.2, 0.2, 15.0, '水果'),
('草莓', '新鲜', '100g', 32, 0.7, 0.3, 7.7, '水果'),
('葡萄', '无籽', '100g', 69, 0.7, 0.2, 18.0, '水果'),
('西瓜', '红瓤', '100g', 30, 0.6, 0.2, 8.0, '水果'),
('猕猴桃', '绿心', '1个(75g)', 46, 0.8, 0.3, 11.0, '水果'),
('蓝莓', '新鲜', '100g', 57, 0.7, 0.3, 14.0, '水果'),
('牛油果', '果肉', '100g', 160, 2.0, 15.0, 9.0, '水果'),
-- 豆制品
('豆腐', '北豆腐', '100g', 82, 8.1, 4.2, 3.0, '豆制品'),
('豆浆', '无糖', '250ml', 60, 5.0, 2.5, 5.0, '豆制品'),
('豆腐干', '五香', '100g', 140, 16.0, 7.0, 4.0, '豆制品'),
('腐竹', '干', '100g', 459, 44.6, 21.7, 22.3, '豆制品'),
-- 乳制品
('牛奶', '全脂', '250ml', 150, 8.0, 8.0, 12.0, '乳制品'),
('酸奶', '原味无糖', '100g', 62, 3.3, 3.3, 5.0, '乳制品'),
('奶酪', '切达干酪', '30g', 120, 7.0, 10.0, 0.5, '乳制品'),
('希腊酸奶', '脱脂', '100g', 59, 10.0, 0.4, 3.6, '乳制品'),
('酸奶油', '蘸酱用', '30g', 60, 1.0, 6.0, 1.0, '乳制品'),
-- 油脂类
('橄榄油', '特级初榨', '10g', 88, 0.0, 10.0, 0.0, '油脂'),
('花生油', '压榨', '10g', 88, 0.0, 10.0, 0.0, '油脂'),
-- 调味品
('白砂糖', '精制', '10g', 39, 0.0, 0.0, 10.0, '调味品'),
('蜂蜜', '天然', '10g', 30, 0.0, 0.0, 8.0, '调味品'),
('酱油', '生抽', '10g', 8, 1.0, 0.0, 1.5, '调味品'),
-- 速食类食物数据
('方便面（袋装）','油炸', '1袋(85g)', 450, 9.0, 18.0, 63.0, '速食'),
('速冻水饺（猪肉白菜）','预制', '6个(120g)', 280, 12.0, 10.0, 38.0, '速食'),
('火腿肠','加工', '1根(50g)', 150, 5.0, 10.0, 12.0, '速食'),
('即食鸡胸肉','加工', '100g', 133, 25.0, 3.0, 2.0, '速食'),
('速食粥（香菇鸡肉）','加工', '1杯(250g)', 180, 5.0, 3.5, 32.0, '速食');

-- 插入用户数据 (管理员 + 2个普通用户)
-- 密码 'password' 的 MD5 哈希值为: 5f4dcc3b5aa765d61d8327deb882cf99
INSERT INTO `user` (`user_id`, `username`, `password`, `birth_date`, `gender`, `height`, `activity_level`, `health_goal`, `role`)
VALUES
    (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '1990-01-01', '男性', 180.00, '中等活跃', '维持身材', 'admin'),
    (2, 'user_lose_weight', 'e10adc3949ba59abbe56e057f20f883e', '1995-05-15', '女性', 165.00, '轻度活跃', '减重', 'user'),
    (3, 'user_gain_muscle', 'e10adc3949ba59abbe56e057f20f883e', '1998-10-20', '男性', 178.00, '活跃', '增肌', 'user');

-- 为用户2 (user_lose_weight, id=2) 创建一条历史"日志"
-- 1. 创建总览记录 (plan 表, record_id = 1)
INSERT INTO `plan` (`record_id`, `user_id`, `record_date`, `record_type`, `plan_name`, `total_calories`, `total_protein`, `total_fat`, `total_carbs`, `weight`, `notes`)
VALUES
    (1, 2, '2025-06-15', '日志', '我的减重日记', 1455.50, 100.85, 40.80, 167.60, 60.5, '今天运动了30分钟，感觉良好！');

-- 2. 为 record_id = 1 创建4个餐次 (meal 表)
INSERT INTO `meal` (`record_id`, `meal_type`, `meal_time`)
VALUES
    (1, '早餐', '08:00'),
    (1, '午餐', '12:30'),
    (1, '晚餐', '18:30'),
    (1, '加餐', '15:00');

-- 3. 为每个餐次添加具体的食物 (meal_food 表) -- 注意：ID是自动生成的
-- 早餐 (meal_id = 1)
INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (1, 3, 1.00, 148.00, 5.50, 2.40, 25.60),  -- 燕麦片
                                                                                                                                                        (1, 47, 1.00, 150.00, 8.00, 8.00, 12.00); -- 牛奶

-- 午餐 (meal_id = 2)
INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (2, 10, 1.00, 111.00, 2.60, 0.90, 23.00), -- 糙米饭
                                                                                                                                                        (2, 15, 1.50, 247.50, 46.50, 5.40, 0.00),  -- 鸡胸肉 (1.5份)
                                                                                                                                                        (2, 25, 2.00, 70.00, 5.60, 0.80, 14.00);   -- 西兰花 (2份)

-- 晚餐 (meal_id = 3)
INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (3, 13, 1.00, 77.00, 2.00, 0.10, 17.50),   -- 土豆
                                                                                                                                                        (3, 18, 1.00, 208.00, 20.00, 13.00, 0.00), -- 三文鱼
                                                                                                                                                        (3, 29, 1.50, 22.50, 1.05, 0.30, 5.40);    -- 黄瓜 (1.5份)

-- 加餐 (meal_id = 4)
INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (4, 50, 1.50, 88.50, 15.00, 0.60, 5.40),   -- 希腊酸奶 (1.5份)
                                                                                                                                                        (4, 38, 1.00, 32.00, 0.70, 0.30, 7.70);    -- 草莓

-- 为用户3 (user_gain_muscle, id=3) 创建一条历史"日志"和一条"计划"
-- A. 创建历史"日志" (record_id = 2)
INSERT INTO `plan` (`record_id`, `user_id`, `record_date`, `record_type`, `plan_name`, `total_calories`, `total_protein`, `total_fat`, `total_carbs`, `weight`)
VALUES
    (2, 3, '2025-06-16', '日志', '增肌第1天', 2259.50, 148.20, 79.55, 237.60, 75.0);

INSERT INTO `meal` (`record_id`, `meal_type`) VALUES
                                                  (2, '早餐'), (2, '午餐'), (2, '晚餐');

INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (5, 2, 2.00, 184.00, 7.80, 2.40, 32.20),   -- 全麦面包 (2片)
                                                                                                                                                        (5, 20, 3.00, 234.00, 18.90, 15.90, 1.80), -- 鸡蛋 (3个)
                                                                                                                                                        (6, 1, 2.00, 260.00, 5.40, 0.60, 56.40),   -- 白米饭 (2份)
                                                                                                                                                        (6, 16, 1.50, 375.00, 39.00, 22.50, 0.00), -- 瘦牛肉 (1.5份)
                                                                                                                                                        (7, 4, 1.50, 556.50, 19.50, 2.25, 112.50), -- 意大利面 (1.5份)
                                                                                                                                                        (7, 49, 1.00, 120.00, 7.00, 10.00, 0.50),  -- 奶酪
                                                                                                                                                        (7, 47, 2.00, 300.00, 16.0, 16.0, 24.0);  -- 牛奶 (2份)

-- B. 创建待执行"计划" (record_id = 3)
INSERT INTO `plan` (`record_id`, `user_id`, `record_date`, `record_type`, `plan_name`, `total_calories`, `total_protein`, `total_fat`, `total_carbs`)
VALUES
    (3, 3, '2025-06-17', '计划', '系统推荐增肌计划', 2394.00, 180.95, 80.70, 228.70);

INSERT INTO `meal` (`record_id`, `meal_type`) VALUES
                                                  (3, '早餐'), (3, '午餐'), (3, '晚餐'), (3, '加餐');

INSERT INTO `meal_food` (`meal_id`, `food_id`, `serving_amount`, `calculated_calories`, `calculated_protein`, `calculated_fat`, `calculated_carbs`) VALUES
                                                                                                                                                        (8, 1, 1.00, 130.00, 2.70, 0.30, 28.20),   -- 白米饭
                                                                                                                                                        (8, 20, 2.00, 156.00, 12.60, 10.60, 1.20), -- 鸡蛋 (2个)
                                                                                                                                                        (9, 10, 1.50, 166.50, 3.90, 1.35, 34.50),  -- 糙米饭 (1.5份)
                                                                                                                                                        (9, 23, 2.00, 330.00, 54.00, 12.00, 0.00), -- 鸡腿肉(去皮) (2份)
                                                                                                                                                        (9, 25, 2.00, 70.00, 5.60, 0.80, 14.00),  -- 西兰花
                                                                                                                                                        (10, 1, 1.50, 195.00, 4.05, 0.45, 42.30),  -- 白米饭 (1.5份)
                                                                                                                                                        (10, 19, 1.50, 148.50, 36.00, 0.45, 0.30), -- 虾仁 (1.5份)
                                                                                                                                                        (10, 26, 1.00, 23, 2.9, 0.4, 3.6),        -- 菠菜
                                                                                                                                                        (11, 36, 2.00, 178.00, 2.20, 0.60, 46.00), -- 香蕉 (2根)
                                                                                                                                                        (11, 48, 2.00, 124, 6.6, 6.6, 10.0);       -- 酸奶 (2份)