/*
 Navicat Premium Data Transfer

 Source Server         : TencentCloud
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : 121.4.179.240:3306
 Source Schema         : mylouise

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 11/10/2021 23:43:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `creat_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建日期',
  `isenabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  `sex` int(0) NULL DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_credit_info
-- ----------------------------
DROP TABLE IF EXISTS `t_credit_info`;
CREATE TABLE `t_credit_info`  (
  `credit_total` int(0) NOT NULL COMMENT '系统目前总credit数 用户credit + 系统credit储备',
  `credit_backup` int(0) NULL DEFAULT NULL COMMENT '系统credit储备',
  `credit_recover` int(0) NULL DEFAULT NULL COMMENT '[周期]/[回复量] 用户每周期credit基础回复量 从系统credit储备中取'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_feature_info
-- ----------------------------
DROP TABLE IF EXISTS `t_feature_info`;
CREATE TABLE `t_feature_info`  (
  `feature_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '功能主键ID',
  `type` int(0) NULL DEFAULT NULL COMMENT '系统功能，开放功能 是否面向QQ用户开放',
  `feature_url` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能调用URL',
  `feature_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能名称',
  `feature_cmd` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能调用命令',
  `credit_cost` int(0) NULL DEFAULT NULL COMMENT '调用功能所需credit',
  `invoke_limit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '[周期]/[次数]功能周期内调用上限',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能一句话说明',
  `info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '功能详细说明',
  `is_original` int(0) NULL DEFAULT NULL COMMENT '功能来源系统本身或插件',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '功能是否启用',
  `avatar` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件头像',
  PRIMARY KEY (`feature_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_feature_static
-- ----------------------------
DROP TABLE IF EXISTS `t_feature_static`;
CREATE TABLE `t_feature_static`  (
  `invoke_id` int(0) NOT NULL COMMENT '功能调用记录主键',
  `feature_id` int(0) NULL DEFAULT NULL COMMENT '功能ID外键',
  `invoke_time` datetime(0) NULL DEFAULT NULL COMMENT '功能调用日期 最低精确到周',
  `count` int(0) NULL DEFAULT NULL COMMENT '调用次数',
  PRIMARY KEY (`invoke_id`) USING BTREE,
  INDEX `FK_1`(`feature_id`) USING BTREE,
  CONSTRAINT `FK_1` FOREIGN KEY (`feature_id`) REFERENCES `t_feature_info` (`feature_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group`  (
  `group_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'QQ群号',
  `role_id` int(0) NULL DEFAULT NULL COMMENT '权限等级ID',
  `member_count` int(0) NULL DEFAULT NULL COMMENT '成员数量',
  `group_memo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群备注',
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群名',
  `group_level` int(0) NULL DEFAULT NULL COMMENT '群等级',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  `avatar` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群头像',
  PRIMARY KEY (`group_id`) USING BTREE,
  INDEX `GROUP_FK_1`(`role_id`) USING BTREE,
  CONSTRAINT `GROUP_FK_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_member_role
-- ----------------------------
DROP TABLE IF EXISTS `t_member_role`;
CREATE TABLE `t_member_role`  (
  `number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'QQ号或群号',
  `type` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组或个人',
  `role_id` int(0) NULL DEFAULT NULL COMMENT '角色ID',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '是否启用该角色',
  INDEX `MEMBER_FK_1`(`role_id`) USING BTREE,
  CONSTRAINT `MEMBER_FK_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_plugin_info
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_info`;
CREATE TABLE `t_plugin_info`  (
  `plugin_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '插件自增ID',
  `author` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件名',
  `path` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件路径',
  `class_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类全限定名',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '安装日期',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '一句话说明',
  `info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '详细说明',
  PRIMARY KEY (`plugin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `role_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '角色主键ID',
  `role_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role_feature
-- ----------------------------
DROP TABLE IF EXISTS `t_role_feature`;
CREATE TABLE `t_role_feature`  (
  `role_id` int(0) NOT NULL COMMENT '角色ID主键',
  `feature_id` int(0) NULL DEFAULT NULL COMMENT '功能ID',
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  INDEX `ROLE_FEATURE_FK_1`(`role_id`) USING BTREE,
  INDEX `ROLE_FEATURE_FK_2`(`feature_id`) USING BTREE,
  CONSTRAINT `ROLE_FEATURE_FK_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ROLE_FEATURE_FK_2` FOREIGN KEY (`feature_id`) REFERENCES `t_feature_info` (`feature_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_config`;
CREATE TABLE `t_sys_config`  (
  `config_id` int(0) NOT NULL AUTO_INCREMENT,
  `type` int(0) NULL DEFAULT NULL,
  `config_key` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `config_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `config_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_scheduled
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_scheduled`;
CREATE TABLE `t_sys_scheduled`  (
  `schedule_id` int(0) NOT NULL COMMENT '定时任务主键ID',
  `schedule_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '定时任务名称',
  `run` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '周期执行',
  `type` int(0) NULL DEFAULT NULL COMMENT '任务类型 区分为系统任务，发送消息，执行功能',
  `target` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务目标 以URL的形式调用系统接口实现',
  `is_return` int(0) NULL DEFAULT NULL COMMENT '是否需要将结果返回到QQ',
  `is_parameter` int(0) NULL DEFAULT NULL COMMENT '是否需要参数',
  `sender_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '在需要返回结果条件下的参数 返回到群聊或私聊',
  `number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '在需要返回结果条件下的参数 返回到的QQ',
  `parameter` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '在需要参数的条件下的参数 任务目标所需的参数',
  `is_enabled` int(0) NULL DEFAULT NULL COMMENT '任务是否启动',
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '定时任务的说明',
  PRIMARY KEY (`schedule_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_upload_info
-- ----------------------------
DROP TABLE IF EXISTS `t_upload_info`;
CREATE TABLE `t_upload_info`  (
  `id` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键ID',
  `user_id` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传用户的QQ',
  `file_type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传的文件类型',
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件的大小',
  `file_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件的描述信息',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '可以补充文件的加密信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `edit_time` datetime(0) NULL DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `UPLAOD_FK_1`(`user_id`) USING BTREE,
  CONSTRAINT `UPLAOD_FK_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户的QQ号',
  `role_id` int(0) NULL DEFAULT NULL COMMENT '权限等级ID',
  `group_id` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户所在的群号',
  `avatar` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户QQ昵称',
  `join_time` datetime(0) NULL DEFAULT NULL COMMENT '用户加群的时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '用户注册Louise的时间',
  `count_setu` int(0) NULL DEFAULT NULL COMMENT '用户请求色图的总次数',
  `count_upload` int(0) NULL DEFAULT NULL COMMENT '用户上传文件的总次数',
  `isenabled` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  `credit` int(0) NULL DEFAULT NULL,
  `credit_buff` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `USER_FK_1`(`role_id`) USING BTREE,
  CONSTRAINT `USER_FK_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_credit
-- ----------------------------
DROP TABLE IF EXISTS `t_user_credit`;
CREATE TABLE `t_user_credit`  (
  `credit_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '变动记录ID主键',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变动用户的QQ',
  `type` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变动类型',
  `number` int(0) NULL DEFAULT NULL COMMENT '变动数值',
  `credit_left` int(0) NULL DEFAULT NULL COMMENT '此次变动用户剩余credit',
  `creat_time` datetime(0) NULL DEFAULT NULL COMMENT 'credit变动日期',
  PRIMARY KEY (`credit_id`) USING BTREE,
  INDEX `CREDIT_FK_1`(`user_id`) USING BTREE,
  CONSTRAINT `CREDIT_FK_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_invoke
-- ----------------------------
DROP TABLE IF EXISTS `t_user_invoke`;
CREATE TABLE `t_user_invoke`  (
  `feature_id` int(0) NOT NULL COMMENT '调用的功能ID',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调用用户的QQ',
  `invoke_time` datetime(0) NULL DEFAULT NULL COMMENT '调用时间',
  PRIMARY KEY (`feature_id`) USING BTREE,
  INDEX `INVOKE_FK_2`(`user_id`) USING BTREE,
  CONSTRAINT `INVOKE_FK_1` FOREIGN KEY (`feature_id`) REFERENCES `t_feature_info` (`feature_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `INVOKE_FK_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
