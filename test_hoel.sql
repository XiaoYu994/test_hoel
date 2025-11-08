/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80037
 Source Host           : localhost:3306
 Source Schema         : test_hoel

 Target Server Type    : MySQL
 Target Server Version : 80037
 File Encoding         : 65001

 Date: 08/11/2025 16:35:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` int(0) NULL DEFAULT NULL COMMENT '性别',
  `head` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '管理员头像路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '管理员' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', '张峰', 1, '2025/09/28/3737e9dec3964213a4f7f144c25d679b.jpg');
INSERT INTO `admin` VALUES (3, 'ceshi', '123456', '111', 1, '2025/09/28/b8724bddc48247cd99ecbd832b0c1e8c.jpg');

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `member_id` int(0) NULL DEFAULT NULL COMMENT '用户',
  `room_id` int(0) NULL DEFAULT NULL COMMENT '房间',
  `start_time` date NULL DEFAULT NULL COMMENT '入住时间',
  `days` int(0) NULL DEFAULT NULL COMMENT '居住天数',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '已付金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '预订' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (19, 4, 1, '2023-11-09', 1, 2, NULL, 300.00);
INSERT INTO `appointment` VALUES (22, 4, 5, '2024-09-27', 2, 1, NULL, 600.00);
INSERT INTO `appointment` VALUES (23, 3, 5, '2025-05-18', 1, 1, NULL, 600.00);
INSERT INTO `appointment` VALUES (24, 3, 4, '2025-05-18', 1, 1, NULL, 350.00);
INSERT INTO `appointment` VALUES (25, 3, 2, '2025-05-19', 1, 2, NULL, 350.00);
INSERT INTO `appointment` VALUES (27, 3, 7, '2025-05-15', 2, 2, NULL, 300.00);
INSERT INTO `appointment` VALUES (28, 4, 8, '2025-05-16', 3, 2, NULL, 350.00);
INSERT INTO `appointment` VALUES (29, 7, 9, '2025-05-17', 1, 3, '客户取消预订', 600.00);
INSERT INTO `appointment` VALUES (30, 3, 10, '2025-05-18', 2, 1, NULL, 450.00);
INSERT INTO `appointment` VALUES (31, 4, 11, '2025-05-17', 4, 2, NULL, 680.00);
INSERT INTO `appointment` VALUES (32, 7, 12, '2025-05-16', 2, 1, NULL, 120.00);
INSERT INTO `appointment` VALUES (34, 4, 6, '2025-05-13', 2, 2, NULL, 120.00);
INSERT INTO `appointment` VALUES (39, 3, 6, '2025-05-20', 2, 2, NULL, 120.00);
INSERT INTO `appointment` VALUES (40, 3, 1, '2025-09-17', 1, 2, NULL, 300.00);
INSERT INTO `appointment` VALUES (41, 3, 9, '2025-09-18', 1, 1, NULL, 600.00);
INSERT INTO `appointment` VALUES (42, 3, 1, '2025-09-27', 1, 1, NULL, 300.00);
INSERT INTO `appointment` VALUES (43, 3, 2, '2025-09-27', 1, 1, NULL, 350.00);
INSERT INTO `appointment` VALUES (44, 3, 1, '2025-09-28', 1, 1, NULL, 300.00);
INSERT INTO `appointment` VALUES (45, 3, 1, '2025-10-01', 4, 1, NULL, 300.00);
INSERT INTO `appointment` VALUES (46, 3, 7, '2025-09-30', 1, 1, NULL, 300.00);
INSERT INTO `appointment` VALUES (47, 3, 10, '2025-09-27', 1, 2, NULL, 450.00);
INSERT INTO `appointment` VALUES (48, 3, 7, '2025-09-28', 1, 3, '测试取消', 300.00);
INSERT INTO `appointment` VALUES (49, 3, 7, '2025-09-28', 2, 1, NULL, 300.00);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `category_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '房间类型',
  `photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '房间图片',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `live_num` int(0) NULL DEFAULT NULL COMMENT '可住人数',
  `bed_num` int(0) NULL DEFAULT NULL COMMENT '床位数',
  `area` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '面积',
  `introduce` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '介绍',
  `deposit` decimal(10, 2) NULL DEFAULT NULL COMMENT '押金',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '房型' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (2, '豪华单人房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d084932e335633e98333.jpg', 300.00, 1, 1, '30', '豪华单人房', 150.00);
INSERT INTO `category` VALUES (3, '经济双人房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d0b2932e335633e98334.jpg', 350.00, 1, 2, '20', '经济双人房', 150.00);
INSERT INTO `category` VALUES (4, '总统套房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d0bd932e335633e98335.jpg', 600.00, 2, 2, '20', '总统套房', 400.00);
INSERT INTO `category` VALUES (5, '经济单人房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d0c5932e335633e98336.png', 120.00, 1, 1, '30', '经济单人房', 100.00);
INSERT INTO `category` VALUES (6, '豪华双人房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d0cc932e335633e98337.png', 450.00, 2, 2, '35', '豪华双人房，设施齐全，视野开阔', 300.00);
INSERT INTO `category` VALUES (7, '豪华双人房', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836d0d4932e335633e98338.jpg', 450.00, 2, 2, '35', '豪华双人房，设施齐全，视野开阔', 300.00);

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` int(0) NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `idcard` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `head` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (3, 'user', '123456', '用户', 1, '18112345670', '430223197809127890', '2025/09/28/48e7de2e6c10487eb1c1f060d897bd9d.jpg', '2025-08-20 17:25:08');
INSERT INTO `member` VALUES (4, 'test', '123456', '张三', 1, '17612345678', '430223197809121234', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836e17d932e335633e9833a.jpg', '2025-09-20 17:26:09');
INSERT INTO `member` VALUES (5, 'lisi', '123456', '李四', 1, '13666666666', '423345656565566666', 'https://longshengwangluo-1306719837.cos.ap-nanjing.myqcloud.com//images2025/05/28/6836e13b932e335633e98339.jpg', '2025-05-28 18:11:09');
INSERT INTO `member` VALUES (6, 'cell', '123456', '测试', 2, '19089909090', '430224200709908890', '2025/09/28/5255653aa40c49dd9b43750dc56696be.jpg', '2025-09-28 08:50:07');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `member_id` int(0) NULL DEFAULT NULL COMMENT '用户',
  `comment` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '留言',
  `reply` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '回复',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '留言时间',
  `reply_time` datetime(0) NULL DEFAULT NULL COMMENT '回复时间',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '留言' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 3, '很好的住宿体验！', '谢谢光临！', '2025-08-23 09:52:07', '2025-09-18 22:12:23', 2);
INSERT INTO `message` VALUES (2, 4, '性价比很高', '欢迎下次光临！', '2025-07-21 09:56:15', '2025-08-23 09:56:20', 2);
INSERT INTO `message` VALUES (4, 4, '希望能提供更多的早餐选择', '已收到您的建议，我们会改进的', '2025-05-16 08:45:22', '2025-05-16 10:15:33', 2);
INSERT INTO `message` VALUES (5, 3, '很好的住宿体验！', '谢谢光临！', '2025-05-17 22:10:05', '2025-05-19 11:46:17', 2);
INSERT INTO `message` VALUES (6, 3, '很好的住宿体验！', '谢谢光临！', '2025-05-19 11:42:58', '2025-05-19 12:18:30', 2);
INSERT INTO `message` VALUES (7, 3, '111', NULL, '2025-09-27 21:18:43', NULL, NULL);
INSERT INTO `message` VALUES (8, 3, '测试', '测试回复', '2025-09-28 09:29:25', '2025-09-28 10:57:30', 2);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '公告内容',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '通知' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '一、 本公司董事会、监事会及董事、监事、高级管理人员保证半年度报告内容的真实性、准确性、完整\n    性，不存在虚假记载、误导性陈述或重大遗漏，并承担个别和连带的法律责任。\n\n二、 本公司第十届董事会第十八次会议于 2025 年 8 月 28 日审议通过了本半年度报告。公司全体董事\n    出席董事会会议。\n三、 本半年度报告未经审计。本公司按中国企业会计准则编制 2023 年半年度财务报表，经德勤华永会\n    计师事务所（特殊普通合伙）审阅，并出具了德师报(阅)字(23)第 R00044 号标准无保留意见的审\n    阅报告。\n四、 公司负责人董事长张晓强先生、首席执行官沈莉女士、主管会计工作负责人首席财务官艾耕云及\n    会计机构负责人吴琳女士声明：保证半年度报告中财务报告的真实、准确、完整。', '2025-07-23 09:06:41', '2025年半年度报告');
INSERT INTO `notice` VALUES (3, ' 时尚之旅酒店管理有限公司（以下简称“时尚之旅”或“标的股权”）为上海锦江国际酒店股份有限公司（以下简称“公司”）全资子公司，拥有 21 家酒店物业。公司所持时尚之旅 100%股权的评估价值为人民币 165,498.89 万元，公司将以不低于经国资备案的评估价值作为挂牌底价，最终交易价格根据公开挂牌结果确定。\n\n根据国有资产管理的有关规定，在正式挂牌转让前，公司将在上海联合产权交易所进行预挂牌。\n\n本次股权转让事项不构成《上市公司重大资产重组管理办法》规定的重大资产重组。', '2025-07-27 10:59:14', '关于挂牌转让时尚之旅酒店管理有限公司 100%股权的公告');
INSERT INTO `notice` VALUES (4, '一、 本公司董事会、监事会及董事、监事、高级管理人员保证半年度报告内容的真实性、准确性、完整\n    性，不存在虚假记载、误导性陈述或重大遗漏，并承担个别和连带的法律责任。\n\n二、 本公司第十届董事会第十八次会议于 2025 年 8 月 28 日审议通过了本半年度报告。公司全体董事\n    出席董事会会议。\n三、 本半年度报告未经审计。本公司按中国企业会计准则编制 2023 年半年度财务报表，经德勤华永会\n    计师事务所（特殊普通合伙）审阅，并出具了德师报(阅)字(23)第 R00044 号标准无保留意见的审\n    阅报告。\n四、 公司负责人董事长张晓强先生、首席执行官沈莉女士、主管会计工作负责人首席财务官艾耕云及\n    会计机构负责人吴琳女士声明：保证半年度报告中财务报告的真实、准确、完整。', '2025-07-27 14:24:14', '2025 年半年度报告');
INSERT INTO `notice` VALUES (5, '11111', '2025-09-28 09:48:10', '测试');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `member_id` int(0) NULL DEFAULT NULL COMMENT '用户',
  `room_id` int(0) NULL DEFAULT NULL COMMENT '房间',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '入住时间',
  `days` int(0) NULL DEFAULT NULL COMMENT '居住天数',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态(1-已预订 2-已入住 3-已退房)',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `check_out_time` datetime(0) NULL DEFAULT NULL COMMENT '退房时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '入住' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (10, 3, 2, '2025-05-19 00:00:00', 1, 3, NULL, 350.00, NULL);
INSERT INTO `orders` VALUES (11, 4, 8, '2025-05-16 00:00:00', 3, 2, NULL, 350.00, NULL);
INSERT INTO `orders` VALUES (12, 7, 6, '2025-05-13 00:00:00', 2, 3, '客人提前退房', 120.00, '2025-05-14 10:30:00');
INSERT INTO `orders` VALUES (13, 3, 7, '2025-05-15 00:00:00', 2, 2, NULL, 300.00, NULL);
INSERT INTO `orders` VALUES (14, 4, 11, '2025-05-17 00:00:00', 4, 1, NULL, 680.00, NULL);
INSERT INTO `orders` VALUES (15, 3, 10, '2025-05-13 00:00:00', 1, 3, NULL, 450.00, '2025-05-14 09:15:00');
INSERT INTO `orders` VALUES (17, 4, 6, '2025-05-12 00:00:00', 1, 3, NULL, 120.00, '2025-05-13 11:45:00');
INSERT INTO `orders` VALUES (18, 3, 12, '2025-05-18 00:00:00', 3, 1, NULL, 120.00, NULL);
INSERT INTO `orders` VALUES (19, 3, 6, '2025-05-20 00:00:00', 2, 2, NULL, 120.00, NULL);
INSERT INTO `orders` VALUES (21, 3, 10, '2025-09-27 00:00:00', 1, 3, '12345', 450.00, NULL);

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `room_num` int(0) NULL DEFAULT NULL COMMENT '房间号',
  `category_id` int(0) NULL DEFAULT NULL COMMENT '房型',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '客房' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 101, 2, 1);
INSERT INTO `room` VALUES (2, 102, 3, 1);
INSERT INTO `room` VALUES (3, 103, 4, 1);
INSERT INTO `room` VALUES (4, 201, 3, 1);
INSERT INTO `room` VALUES (5, 202, 4, 1);
INSERT INTO `room` VALUES (6, 203, 5, 1);
INSERT INTO `room` VALUES (7, 301, 2, 1);
INSERT INTO `room` VALUES (8, 302, 3, 1);
INSERT INTO `room` VALUES (9, 303, 4, 1);
INSERT INTO `room` VALUES (10, 401, 6, 1);
INSERT INTO `room` VALUES (11, 402, 7, 1);
INSERT INTO `room` VALUES (12, 501, 5, 1);
INSERT INTO `room` VALUES (13, 111, 2, 1);

SET FOREIGN_KEY_CHECKS = 1;
