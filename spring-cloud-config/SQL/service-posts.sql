/*
SQLyog  v12.2.6 (64 bit)
MySQL - 5.7.22 : Database - itoken-service-admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`service-posts` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `service-posts`;

DROP TABLE IF EXISTS tb_posts_post;

-- 文章表
CREATE TABLE tb_posts_post
(
	post_guid VARCHAR(100) NOT NULL COMMENT '文章编码',
    title VARCHAR(100) NOT NULL COMMENT '文章标题',
    time_published DATETIME NOT NULL COMMENT '文章发布时间',
    STATUS CHAR NOT NULL COMMENT '文章状态（0草稿 1已发布的文章 2待审核的文章 3被拒绝文章 4定时发布的文章）',
    alias VARCHAR(100) COMMENT '文章别名',
    score DECIMAL(3) DEFAULT 0 COMMENT '文章得分',
    summary TEXT COMMENT '文章摘要',
    main TEXT COMMENT '文章正文',
    AUTHORS TEXT COMMENT '文章作者对象',
    thumb_image TEXT COMMENT '封面缩略图片',
    original_images TEXT COMMENT '裁剪后不带尺寸的正文图片数组',
    images TEXT COMMENT '裁剪后带尺寸的正文图片数组',
    full_size_images TEXT COMMENT '裁剪前的正文图片数组',
    tags TEXT COMMENT '文章标签',
    v_tags TEXT COMMENT '文章特色标签',
    number_of_upvotes DECIMAL(9) DEFAULT 0 COMMENT '被赞数',
    number_of_downvotes DECIMAL(9) DEFAULT 0 COMMENT '被踩数',
    number_of_reads DECIMAL(9) DEFAULT 0 COMMENT '被阅读数',
    number_of_shares DECIMAL(9) DEFAULT 0 COMMENT '被分享数',
    number_of_bookmarks DECIMAL(9) DEFAULT 0 COMMENT '被收藏数',
    number_of_comments DECIMAL(9) DEFAULT 0 COMMENT '被评论数',
    reject_msg VARCHAR(100) COMMENT '文章审核被拒理由',
    series TEXT COMMENT '一篇文章的系列集合',
    access CHAR(2) COMMENT '文章的阅读权限（0无限制 1会员）',
	create_by VARCHAR(64) NOT NULL COMMENT '创建者',
	create_date DATETIME NOT NULL COMMENT '创建时间',
	update_by VARCHAR(64) NOT NULL COMMENT '更新者',
	update_date DATETIME NOT NULL COMMENT '更新时间',
	remarks VARCHAR(500) COMMENT '备注信息',
	extend_s1 VARCHAR(500) COMMENT '扩展 String 1',
	extend_s2 VARCHAR(500) COMMENT '扩展 String 2',
	extend_s3 VARCHAR(500) COMMENT '扩展 String 3',
	extend_s4 VARCHAR(500) COMMENT '扩展 String 4',
	extend_s5 VARCHAR(500) COMMENT '扩展 String 5',
	extend_s6 VARCHAR(500) COMMENT '扩展 String 6',
	extend_s7 VARCHAR(500) COMMENT '扩展 String 7',
	extend_s8 VARCHAR(500) COMMENT '扩展 String 8',
	extend_i1 DECIMAL(19) COMMENT '扩展 Integer 1',
	extend_i2 DECIMAL(19) COMMENT '扩展 Integer 2',
	extend_i3 DECIMAL(19) COMMENT '扩展 Integer 3',
	extend_i4 DECIMAL(19) COMMENT '扩展 Integer 4',
	extend_f1 DECIMAL(19,4) COMMENT '扩展 Float 1',
	extend_f2 DECIMAL(19,4) COMMENT '扩展 Float 2',
	extend_f3 DECIMAL(19,4) COMMENT '扩展 Float 3',
	extend_f4 DECIMAL(19,4) COMMENT '扩展 Float 4',
	extend_d1 DATETIME COMMENT '扩展 Date 1',
	extend_d2 DATETIME COMMENT '扩展 Date 2',
	extend_d3 DATETIME COMMENT '扩展 Date 3',
	extend_d4 DATETIME COMMENT '扩展 Date 4',
	PRIMARY KEY (post_guid)
) COMMENT = '文章表';

CREATE INDEX idx_posts_post_pg ON tb_posts_post (post_guid ASC);