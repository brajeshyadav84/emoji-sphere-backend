-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: emoji_sphere
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_response` text,
  `created_at` datetime(6) NOT NULL,
  `message` text,
  `status` varchar(255) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
INSERT INTO `feedbacks` VALUES (1,NULL,'2025-10-21 07:33:08.854544','testin feedback','open','test','general',9);
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp_verifications`
--

DROP TABLE IF EXISTS `otp_verifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_verifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `otp` varchar(255) NOT NULL,
  `verified` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_verifications`
--

LOCK TABLES `otp_verifications` WRITE;
/*!40000 ALTER TABLE `otp_verifications` DISABLE KEYS */;
INSERT INTO `otp_verifications` VALUES (1,'2025-10-12 03:37:07.567885','2025-10-12 03:42:07.552883',NULL,'790974',_binary '\0','raj@gmail.com'),(2,'2025-10-12 03:37:41.140358','2025-10-12 03:42:41.133354',NULL,'668705',_binary '\0','raj@gmail.com'),(5,'2025-10-12 04:28:24.996248','2025-10-12 04:33:24.991247',NULL,'560439',_binary '\0','brajesh.mymailbox@gmail.com'),(6,'2025-10-12 04:29:27.643151','2025-10-12 04:34:27.638151',NULL,'952399',_binary '','brajesh.mymailbox@gmail.com'),(7,'2025-10-13 02:02:19.213654','2025-10-13 02:07:19.157655',NULL,'052413',_binary '\0','admin@gmail.com'),(8,'2025-10-17 03:28:45.866405','2025-10-17 03:33:45.808344',NULL,'404935',_binary '\0','br11aj@gmail.com'),(9,'2025-10-17 05:08:15.165239','2025-10-17 05:13:15.155240',NULL,'824645',_binary '','tech8talk@gmail.com'),(11,'2025-10-17 07:42:04.429770','2025-10-17 07:47:04.354773',NULL,'228674',_binary '','brajesh.cavisa@gmail.com');
/*!40000 ALTER TABLE `otp_verifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_tags`
--

DROP TABLE IF EXISTS `post_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tags` (
  `post_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`post_id`,`tag_id`),
  KEY `FKl3afdmvgqhrxnq46kbj797yek` (`tag_id`),
  CONSTRAINT `FKl3afdmvgqhrxnq46kbj797yek` FOREIGN KEY (`tag_id`) REFERENCES `tbl_tags` (`id`),
  CONSTRAINT `FKnlglr4yafl1vnvf3ewadaa6c4` FOREIGN KEY (`post_id`) REFERENCES `tbl_posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_tags`
--

LOCK TABLES `post_tags` WRITE;
/*!40000 ALTER TABLE `post_tags` DISABLE KEYS */;
INSERT INTO `post_tags` VALUES (27,21),(25,22);
/*!40000 ALTER TABLE `post_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` enum('ROLE_USER','ROLE_MODERATOR','ROLE_ADMIN') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_categories`
--

DROP TABLE IF EXISTS `tbl_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `icon` varchar(10) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_categories`
--

LOCK TABLES `tbl_categories` WRITE;
/*!40000 ALTER TABLE `tbl_categories` DISABLE KEYS */;
INSERT INTO `tbl_categories` VALUES (1,'General','💬',NULL,NULL,NULL),(2,'Humor','😂',NULL,NULL,NULL),(3,'Art','🎨',NULL,NULL,NULL),(4,'Technology','💻',NULL,NULL,NULL),(5,'Games','🎮',NULL,NULL,NULL),(6,'Food','🍕',NULL,NULL,NULL),(7,'Travel','✈️',NULL,NULL,NULL),(8,'Music','🎵',NULL,NULL,NULL),(9,'Sports','⚽',NULL,NULL,NULL),(10,'Education','📚',NULL,NULL,NULL);
/*!40000 ALTER TABLE `tbl_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_chat_conversations`
--

DROP TABLE IF EXISTS `tbl_chat_conversations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_chat_conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_one_id` bigint NOT NULL,
  `user_two_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_pair` (`user_one_id`,`user_two_id`),
  UNIQUE KEY `UKbmciu3krsxytjlhr3n3tkxur3` (`user_one_id`,`user_two_id`),
  KEY `idx_conversations_user_one_id` (`user_one_id`),
  KEY `idx_conversations_user_two_id` (`user_two_id`),
  KEY `idx_conversations_updated_at` (`updated_at`),
  KEY `idx_chat_conversations_user_updated` (`user_one_id`,`user_two_id`,`updated_at` DESC),
  CONSTRAINT `tbl_chat_conversations_ibfk_1` FOREIGN KEY (`user_one_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_chat_conversations_ibfk_2` FOREIGN KEY (`user_two_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_conversations_user_order` CHECK ((`user_one_id` < `user_two_id`))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_chat_conversations`
--

LOCK TABLES `tbl_chat_conversations` WRITE;
/*!40000 ALTER TABLE `tbl_chat_conversations` DISABLE KEYS */;
INSERT INTO `tbl_chat_conversations` VALUES (1,9,11,'2025-10-12 08:21:18','2025-10-21 07:51:43'),(5,8,11,'2025-10-12 09:46:20','2025-10-13 00:40:20');
/*!40000 ALTER TABLE `tbl_chat_conversations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_chat_message_status`
--

DROP TABLE IF EXISTS `tbl_chat_message_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_chat_message_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message_id` bigint NOT NULL,
  `delivered_at` datetime DEFAULT NULL,
  `read_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `message_id` (`message_id`),
  KEY `idx_status_message_id` (`message_id`),
  KEY `idx_status_delivered_at` (`delivered_at`),
  KEY `idx_status_read_at` (`read_at`),
  CONSTRAINT `tbl_chat_message_status_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `tbl_chat_messages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_chat_message_status`
--

LOCK TABLES `tbl_chat_message_status` WRITE;
/*!40000 ALTER TABLE `tbl_chat_message_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_chat_message_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_chat_messages`
--

DROP TABLE IF EXISTS `tbl_chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `message_text` varchar(1000) NOT NULL,
  `message_type` enum('TEXT','EMOJI','IMAGE','FILE') NOT NULL DEFAULT 'TEXT',
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_messages_conversation_id` (`conversation_id`),
  KEY `idx_messages_sender_id` (`sender_id`),
  KEY `idx_messages_receiver_id` (`receiver_id`),
  KEY `idx_messages_is_read` (`is_read`),
  KEY `idx_messages_created_at` (`created_at`),
  KEY `idx_messages_conversation_created` (`conversation_id`,`created_at`),
  KEY `idx_chat_messages_conversation_time` (`conversation_id`,`created_at` DESC),
  KEY `idx_chat_messages_user_unread` (`receiver_id`,`is_read`,`conversation_id`),
  CONSTRAINT `tbl_chat_messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `tbl_chat_conversations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_chat_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_chat_messages_ibfk_3` FOREIGN KEY (`receiver_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_chat_messages`
--

LOCK TABLES `tbl_chat_messages` WRITE;
/*!40000 ALTER TABLE `tbl_chat_messages` DISABLE KEYS */;
INSERT INTO `tbl_chat_messages` VALUES (1,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:21:18','2025-10-12 08:21:30'),(2,1,11,9,'hello','TEXT',1,'2025-10-12 08:21:27','2025-10-12 08:21:30'),(3,1,9,11,'👋 Hi there!','EMOJI',1,'2025-10-12 08:21:30','2025-10-12 08:29:26'),(4,1,9,11,'potty boy','TEXT',1,'2025-10-12 08:21:44','2025-10-12 08:29:26'),(5,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:21:47','2025-10-12 08:37:15'),(6,1,9,11,'homework done ?','TEXT',1,'2025-10-12 08:22:11','2025-10-12 08:29:26'),(7,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:22:25','2025-10-12 08:37:15'),(8,1,11,9,'not yet','TEXT',1,'2025-10-12 08:27:19','2025-10-12 08:37:15'),(9,1,9,11,'👋 Hi there!','EMOJI',1,'2025-10-12 08:27:25','2025-10-12 08:29:26'),(10,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:29:25','2025-10-12 08:37:15'),(11,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:29:33','2025-10-12 08:37:15'),(12,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:30:52','2025-10-12 08:37:15'),(13,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:37:34','2025-10-12 08:48:25'),(14,1,11,9,'👋 Hi there!','EMOJI',1,'2025-10-12 08:41:03','2025-10-12 08:48:25'),(15,1,11,9,'whats app','TEXT',1,'2025-10-12 08:48:17','2025-10-12 08:48:25'),(16,1,9,11,'all good','TEXT',1,'2025-10-12 08:48:32','2025-10-12 08:48:38'),(17,1,11,9,'n yoiurs ?🚀','TEXT',1,'2025-10-12 08:48:51','2025-10-12 08:48:55'),(18,1,11,9,'finish math hw','TEXT',1,'2025-10-12 08:55:58','2025-10-12 08:56:08'),(19,1,9,11,'👋 Hi there!','EMOJI',1,'2025-10-12 08:56:08','2025-10-12 09:04:55'),(20,1,11,9,'hmm','TEXT',1,'2025-10-12 09:07:23','2025-10-12 09:07:32'),(21,1,11,9,'u there?','TEXT',1,'2025-10-12 09:08:06','2025-10-12 09:08:11'),(22,1,9,11,'yes','TEXT',1,'2025-10-12 09:08:20','2025-10-12 09:08:28'),(23,1,11,9,'helloo','TEXT',1,'2025-10-12 09:37:48','2025-10-12 09:42:15'),(24,1,9,11,'hi','TEXT',1,'2025-10-12 09:38:02','2025-10-12 09:38:55'),(25,1,9,11,'heheh','TEXT',1,'2025-10-12 09:38:13','2025-10-12 09:38:55'),(26,1,11,9,'hello','TEXT',1,'2025-10-12 09:42:28','2025-10-12 09:42:56'),(27,5,8,11,'hello','TEXT',1,'2025-10-12 09:46:20','2025-10-12 09:46:26'),(28,1,9,11,'dolly here','TEXT',1,'2025-10-12 09:46:46','2025-10-12 09:46:59'),(29,5,8,11,'john here','TEXT',1,'2025-10-12 09:46:55','2025-10-12 09:47:03'),(30,5,11,8,'hi john','TEXT',1,'2025-10-12 09:47:07','2025-10-12 09:47:11'),(31,5,8,11,'hello john GM','TEXT',1,'2025-10-12 09:47:56','2025-10-12 09:48:01'),(32,1,9,11,'beta kaha ho','TEXT',1,'2025-10-12 09:48:11','2025-10-12 09:48:16'),(33,1,9,11,'asdf','TEXT',1,'2025-10-12 12:56:59','2025-10-12 12:57:27'),(34,1,9,11,'qwqwqw','TEXT',1,'2025-10-12 12:57:18','2025-10-12 12:57:27'),(35,1,11,9,'hello','TEXT',1,'2025-10-13 00:38:39','2025-10-13 00:39:13'),(36,5,11,8,'hi','TEXT',0,'2025-10-13 00:40:20','2025-10-13 00:40:20'),(37,1,11,9,'i','TEXT',1,'2025-10-14 12:58:13','2025-10-14 13:10:58'),(38,1,11,9,'kj','TEXT',1,'2025-10-14 13:09:15','2025-10-14 13:10:58'),(39,1,9,11,'eelo u tere ?','TEXT',1,'2025-10-14 14:44:39','2025-10-14 14:45:08'),(40,1,9,11,'awesome','TEXT',1,'2025-10-14 14:50:33','2025-10-14 15:00:24'),(41,1,9,11,'watsa','TEXT',1,'2025-10-14 14:50:54','2025-10-14 15:00:24'),(42,1,9,11,'🎯','EMOJI',1,'2025-10-14 15:00:18','2025-10-14 15:00:24'),(43,1,11,9,'status?','TEXT',1,'2025-10-14 15:00:45','2025-10-14 15:01:11'),(44,1,11,9,'hi','TEXT',1,'2025-10-15 01:56:29','2025-10-15 01:57:29'),(45,1,11,9,'hi','TEXT',1,'2025-10-15 06:05:50','2025-10-15 06:06:07'),(46,1,9,11,'i am here','TEXT',1,'2025-10-15 06:06:33','2025-10-15 06:06:47'),(47,1,9,11,'hi','TEXT',1,'2025-10-18 01:36:46','2025-10-18 12:23:04'),(48,1,9,11,'jyujhgjghj','TEXT',1,'2025-10-18 01:39:06','2025-10-18 12:23:04'),(49,1,9,11,'ello','TEXT',1,'2025-10-18 10:42:54','2025-10-18 12:23:04'),(50,1,9,11,'i knoe you','TEXT',1,'2025-10-18 12:23:28','2025-10-18 12:24:01'),(51,1,11,9,'yes','TEXT',1,'2025-10-18 12:23:47','2025-10-18 12:24:32'),(52,1,11,9,'ello','TEXT',1,'2025-10-21 07:51:30','2025-10-21 07:51:38'),(53,1,9,11,'s','TEXT',0,'2025-10-21 07:51:43','2025-10-21 07:51:43');
/*!40000 ALTER TABLE `tbl_chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_chat_user_blocklist`
--

DROP TABLE IF EXISTS `tbl_chat_user_blocklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_chat_user_blocklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blocker_id` bigint NOT NULL,
  `blocked_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_block_pair` (`blocker_id`,`blocked_id`),
  UNIQUE KEY `UKd6c1d8ytdwgqrtyjhe0milu89` (`blocker_id`,`blocked_id`),
  KEY `idx_blocklist_blocker_id` (`blocker_id`),
  KEY `idx_blocklist_blocked_id` (`blocked_id`),
  CONSTRAINT `tbl_chat_user_blocklist_ibfk_1` FOREIGN KEY (`blocker_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_chat_user_blocklist_ibfk_2` FOREIGN KEY (`blocked_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_blocklist_no_self_block` CHECK ((`blocker_id` <> `blocked_id`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_chat_user_blocklist`
--

LOCK TABLES `tbl_chat_user_blocklist` WRITE;
/*!40000 ALTER TABLE `tbl_chat_user_blocklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_chat_user_blocklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_comments`
--

DROP TABLE IF EXISTS `tbl_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `comment_text` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `likes_count` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  KEY `FKthmwaxpw968h7vkwwhpydjoms` (`parent_comment_id`),
  CONSTRAINT `FKthmwaxpw968h7vkwwhpydjoms` FOREIGN KEY (`parent_comment_id`) REFERENCES `tbl_comments` (`id`),
  CONSTRAINT `tbl_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `tbl_posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_comments`
--

LOCK TABLES `tbl_comments` WRITE;
/*!40000 ALTER TABLE `tbl_comments` DISABLE KEYS */;
INSERT INTO `tbl_comments` VALUES (1,11,9,'hi dear','2025-10-10 08:46:04','2025-10-12 13:23:55',NULL,3),(2,13,9,'hi good afternoon','2025-10-10 10:41:27','2025-10-12 23:55:46',NULL,1),(3,11,9,'he yes','2025-10-10 10:56:53','2025-10-15 06:02:35',1,3),(4,11,9,'hello','2025-10-10 11:07:44','2025-10-15 06:02:39',NULL,2),(5,10,11,'hello','2025-10-11 08:42:33','2025-10-21 06:43:15',NULL,0),(6,20,11,'hi','2025-10-15 02:48:24','2025-10-15 02:48:24',NULL,0),(7,11,9,'gfhgf','2025-10-17 10:25:29','2025-10-17 10:25:29',NULL,0),(8,10,9,'hi dear','2025-10-18 02:30:55','2025-10-18 02:44:36',NULL,1),(9,10,9,'helo singappore','2025-10-18 02:31:13','2025-10-18 02:44:42',NULL,1);
/*!40000 ALTER TABLE `tbl_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_conversation_settings`
--

DROP TABLE IF EXISTS `tbl_conversation_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_conversation_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `notifications_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `muted_until` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_user` (`conversation_id`,`user_id`),
  UNIQUE KEY `UKk2jdjla7bjhop7upxj1q9tdtr` (`conversation_id`,`user_id`),
  KEY `idx_settings_conversation_id` (`conversation_id`),
  KEY `idx_settings_user_id` (`user_id`),
  KEY `idx_settings_archived` (`archived`),
  KEY `idx_settings_muted_until` (`muted_until`),
  CONSTRAINT `tbl_conversation_settings_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `tbl_chat_conversations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_conversation_settings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_conversation_settings`
--

LOCK TABLES `tbl_conversation_settings` WRITE;
/*!40000 ALTER TABLE `tbl_conversation_settings` DISABLE KEYS */;
INSERT INTO `tbl_conversation_settings` VALUES (1,1,9,1,0,NULL,'2025-10-12 08:21:18','2025-10-12 08:21:18'),(2,1,11,1,0,NULL,'2025-10-12 08:21:18','2025-10-12 08:21:18'),(9,5,8,1,0,NULL,'2025-10-12 09:46:20','2025-10-12 09:46:20'),(10,5,11,1,0,NULL,'2025-10-12 09:46:20','2025-10-12 09:46:20');
/*!40000 ALTER TABLE `tbl_conversation_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_daily_challenges`
--

DROP TABLE IF EXISTS `tbl_daily_challenges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_daily_challenges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `grade_level` varchar(50) NOT NULL,
  `challenge_date` date NOT NULL,
  `points` int DEFAULT '0',
  `description` text,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_daily_challenges`
--

LOCK TABLES `tbl_daily_challenges` WRITE;
/*!40000 ALTER TABLE `tbl_daily_challenges` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_daily_challenges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_daily_questions`
--

DROP TABLE IF EXISTS `tbl_daily_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_daily_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_date` date NOT NULL,
  `category_id` bigint NOT NULL,
  `difficulty` varchar(255) NOT NULL,
  `question` text NOT NULL,
  `answer` text NOT NULL,
  `youtube_video_id` varchar(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `tbl_daily_questions_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `tbl_categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_daily_questions`
--

LOCK TABLES `tbl_daily_questions` WRITE;
/*!40000 ALTER TABLE `tbl_daily_questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_daily_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_friendships`
--

DROP TABLE IF EXISTS `tbl_friendships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_friendships` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user1_id` bigint NOT NULL,
  `user2_id` bigint NOT NULL,
  `status` enum('PENDING','ACCEPTED','DECLINED','BLOCKED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `requester_id` bigint NOT NULL,
  `responder_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `responded_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_friendship_pair` (`user1_id`,`user2_id`),
  UNIQUE KEY `UKqnurf5vwg0y2js2ix5lsoeoa8` (`user1_id`,`user2_id`),
  KEY `fk_friendship_responder` (`responder_id`),
  KEY `idx_user1_status` (`user1_id`,`status`),
  KEY `idx_user2_status` (`user2_id`,`status`),
  KEY `idx_requester` (`requester_id`),
  KEY `idx_status_created` (`status`,`created_at`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_updated_at` (`updated_at`),
  CONSTRAINT `fk_friendship_requester` FOREIGN KEY (`requester_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_friendship_responder` FOREIGN KEY (`responder_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_friendship_user1` FOREIGN KEY (`user1_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_friendship_user2` FOREIGN KEY (`user2_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_different_users` CHECK (((`requester_id` <> `responder_id`) or (`responder_id` is null))),
  CONSTRAINT `chk_requester` CHECK ((`requester_id` in (`user1_id`,`user2_id`))),
  CONSTRAINT `chk_responder` CHECK (((`responder_id` is null) or (`responder_id` in (`user1_id`,`user2_id`)))),
  CONSTRAINT `chk_user_order` CHECK ((`user1_id` < `user2_id`))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Table to store friendship relationships between users with status tracking';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_friendships`
--

LOCK TABLES `tbl_friendships` WRITE;
/*!40000 ALTER TABLE `tbl_friendships` DISABLE KEYS */;
INSERT INTO `tbl_friendships` VALUES (4,8,17,'PENDING',17,NULL,'2025-10-12 12:38:13','2025-10-12 12:38:13',NULL),(8,9,11,'ACCEPTED',9,11,'2025-10-12 15:15:48','2025-10-12 07:16:11','2025-10-12 07:16:11'),(9,8,11,'ACCEPTED',11,8,'2025-10-12 16:28:42','2025-10-12 09:45:34','2025-10-12 09:45:34');
/*!40000 ALTER TABLE `tbl_friendships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_grades`
--

DROP TABLE IF EXISTS `tbl_grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_grades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_grades`
--

LOCK TABLES `tbl_grades` WRITE;
/*!40000 ALTER TABLE `tbl_grades` DISABLE KEYS */;
INSERT INTO `tbl_grades` VALUES (1,'Grade 1'),(10,'Grade 10'),(2,'Grade 2'),(3,'Grade 3'),(4,'Grade 4'),(5,'Grade 5'),(6,'Grade 6'),(7,'Grade 7'),(8,'Grade 8'),(9,'Grade 9');
/*!40000 ALTER TABLE `tbl_grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_group_comments`
--

DROP TABLE IF EXISTS `tbl_group_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_group_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `comment_text` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `likes_count` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  KEY `FK_group_comments_parent` (`parent_comment_id`),
  CONSTRAINT `FK_group_comments_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES `tbl_group_comments` (`id`),
  CONSTRAINT `tbl_group_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `tbl_group_posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_group_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_group_comments`
--

LOCK TABLES `tbl_group_comments` WRITE;
/*!40000 ALTER TABLE `tbl_group_comments` DISABLE KEYS */;
INSERT INTO `tbl_group_comments` VALUES (8,36,9,'he he','2025-10-16 11:12:58','2025-10-16 11:28:14',NULL,1),(9,37,9,'hi','2025-10-16 11:19:59','2025-10-18 02:35:20',NULL,1),(10,37,9,'hello','2025-10-17 02:48:57','2025-10-18 10:38:32',NULL,0),(11,37,9,'hello','2025-10-18 01:15:37','2025-10-18 02:43:56',NULL,1);
/*!40000 ALTER TABLE `tbl_group_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_group_likes`
--

DROP TABLE IF EXISTS `tbl_group_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_group_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `post_id` bigint DEFAULT NULL,
  `comment_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5i6rbj03us0ymgwadhgnhdp7l` (`user_id`,`post_id`),
  UNIQUE KEY `UKl2eb7hvnl5rga6mawoiotkd41` (`user_id`,`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `tbl_group_likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_group_likes_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `tbl_group_posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_group_likes_ibfk_3` FOREIGN KEY (`comment_id`) REFERENCES `tbl_group_comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk__group_like_target` CHECK ((((`post_id` is not null) and (`comment_id` is null)) or ((`post_id` is null) and (`comment_id` is not null))))
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_group_likes`
--

LOCK TABLES `tbl_group_likes` WRITE;
/*!40000 ALTER TABLE `tbl_group_likes` DISABLE KEYS */;
INSERT INTO `tbl_group_likes` VALUES (82,9,NULL,8,'2025-10-16 11:28:14'),(85,9,NULL,9,'2025-10-18 02:35:20'),(87,9,NULL,11,'2025-10-18 02:43:56'),(88,9,37,NULL,'2025-10-18 10:38:27');
/*!40000 ALTER TABLE `tbl_group_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_group_members`
--

DROP TABLE IF EXISTS `tbl_group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_group_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int DEFAULT NULL,
  `group_id` bigint NOT NULL,
  `joined_at` datetime(6) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `dob` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfdpnpackjjnpepoe1qgr72o2j` (`group_id`),
  KEY `FK3m6hcff3uijstctqnigk4oeut` (`user_id`),
  CONSTRAINT `FK3m6hcff3uijstctqnigk4oeut` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`),
  CONSTRAINT `FKfdpnpackjjnpepoe1qgr72o2j` FOREIGN KEY (`group_id`) REFERENCES `tbl_groups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_group_members`
--

LOCK TABLES `tbl_group_members` WRITE;
/*!40000 ALTER TABLE `tbl_group_members` DISABLE KEYS */;
INSERT INTO `tbl_group_members` VALUES (2,40,3,'2025-10-13 07:30:10.735504','MEMBER',18,NULL),(3,25,3,'2025-10-15 01:57:38.523464','MEMBER',9,NULL),(4,40,4,'2025-10-15 08:47:53.275675','ADMIN',18,NULL),(7,8,4,'2025-10-15 10:01:11.671960','MEMBER',11,NULL),(8,NULL,4,'2025-10-17 05:10:00.297407','MEMBER',20,'14/02/2020'),(9,NULL,4,'2025-10-17 10:26:13.459864','MEMBER',9,'14/10/1995');
/*!40000 ALTER TABLE `tbl_group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_group_post_media`
--

DROP TABLE IF EXISTS `tbl_group_post_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_group_post_media` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `media_url` varchar(255) NOT NULL,
  `media_type` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `tbl_group_post_media_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `tbl_group_posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_group_post_media`
--

LOCK TABLES `tbl_group_post_media` WRITE;
/*!40000 ALTER TABLE `tbl_group_post_media` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_group_post_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_group_posts`
--

DROP TABLE IF EXISTS `tbl_group_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_group_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text,
  `media_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `is_public` bit(1) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `likes_count` bigint NOT NULL,
  `group_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `tbl_group_posts_category_id_IDX` (`category_id`) USING BTREE,
  KEY `tbl_group_posts_tbl_groups_FK` (`group_id`),
  CONSTRAINT `FKoogm8755mlr2hmtku4iiidobv` FOREIGN KEY (`category_id`) REFERENCES `tbl_categories` (`id`),
  CONSTRAINT `tbl_group_posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_group_posts_tbl_groups_FK` FOREIGN KEY (`group_id`) REFERENCES `tbl_groups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_group_posts`
--

LOCK TABLES `tbl_group_posts` WRITE;
/*!40000 ALTER TABLE `tbl_group_posts` DISABLE KEYS */;
INSERT INTO `tbl_group_posts` VALUES (36,11,'hello',NULL,'2025-10-15 10:16:50','2025-10-18 10:38:16',NULL,_binary '',NULL,0,4),(37,11,'hi group 3',NULL,'2025-10-15 10:18:40','2025-10-18 10:38:27',NULL,_binary '',NULL,1,3),(42,9,'done🎨',NULL,'2025-10-17 01:31:43','2025-10-17 01:31:43',NULL,_binary '',NULL,0,3),(43,20,'helo',NULL,'2025-10-17 05:10:08','2025-10-17 05:10:08',NULL,_binary '',NULL,0,4),(44,9,'utyutyu',NULL,'2025-10-17 10:25:49','2025-10-17 10:25:49',NULL,_binary '',NULL,0,3);
/*!40000 ALTER TABLE `tbl_group_posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_groups`
--

DROP TABLE IF EXISTS `tbl_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `emoji` varchar(10) DEFAULT NULL,
  `description` text,
  `privacy` varchar(255) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `tbl_groups_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `tbl_users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_groups`
--

LOCK TABLES `tbl_groups` WRITE;
/*!40000 ALTER TABLE `tbl_groups` DISABLE KEYS */;
INSERT INTO `tbl_groups` VALUES (3,'Art Lover','🎨','for art lovers please join','PUBLIC',18,'2025-10-13 03:05:28',NULL),(4,'Football','⚽','make a team','PUBLIC',18,'2025-10-15 08:47:53',NULL);
/*!40000 ALTER TABLE `tbl_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_holiday_assignments`
--

DROP TABLE IF EXISTS `tbl_holiday_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_holiday_assignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `grade_id` bigint NOT NULL,
  `holiday_type` varchar(255) NOT NULL,
  `due_date` date NOT NULL,
  `description` text,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `grade_id` (`grade_id`),
  CONSTRAINT `tbl_holiday_assignments_ibfk_1` FOREIGN KEY (`grade_id`) REFERENCES `tbl_grades` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_holiday_assignments`
--

LOCK TABLES `tbl_holiday_assignments` WRITE;
/*!40000 ALTER TABLE `tbl_holiday_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_holiday_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_jokes`
--

DROP TABLE IF EXISTS `tbl_jokes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_jokes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_jokes`
--

LOCK TABLES `tbl_jokes` WRITE;
/*!40000 ALTER TABLE `tbl_jokes` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_jokes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_likes`
--

DROP TABLE IF EXISTS `tbl_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `post_id` bigint DEFAULT NULL,
  `comment_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKarchqaumeg6cg3cij50mtuqyn` (`user_id`,`post_id`),
  UNIQUE KEY `UKpx33c7qf2vf79bg42sn2o0k41` (`user_id`,`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `tbl_likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_likes_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `tbl_posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_likes_ibfk_3` FOREIGN KEY (`comment_id`) REFERENCES `tbl_comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_like_target` CHECK ((((`post_id` is not null) and (`comment_id` is null)) or ((`post_id` is null) and (`comment_id` is not null))))
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_likes`
--

LOCK TABLES `tbl_likes` WRITE;
/*!40000 ALTER TABLE `tbl_likes` DISABLE KEYS */;
INSERT INTO `tbl_likes` VALUES (21,10,NULL,3,'2025-10-10 13:55:55'),(23,10,10,NULL,'2025-10-10 13:59:06'),(24,10,NULL,1,'2025-10-10 14:02:50'),(25,10,11,NULL,'2025-10-10 14:02:52'),(36,9,13,NULL,'2025-10-11 06:37:50'),(37,9,NULL,1,'2025-10-11 06:38:04'),(38,9,NULL,3,'2025-10-11 06:38:06'),(39,9,NULL,4,'2025-10-11 06:38:07'),(41,17,10,NULL,'2025-10-12 04:37:59'),(42,17,11,NULL,'2025-10-12 04:38:02'),(48,11,NULL,1,'2025-10-12 13:23:55'),(52,11,NULL,2,'2025-10-12 23:55:46'),(60,11,20,NULL,'2025-10-15 02:48:04'),(61,11,21,NULL,'2025-10-15 03:16:53'),(62,11,13,NULL,'2025-10-15 03:16:59'),(65,11,11,NULL,'2025-10-15 03:42:12'),(68,11,10,NULL,'2025-10-15 05:02:56'),(69,11,NULL,3,'2025-10-15 06:02:35'),(70,11,NULL,4,'2025-10-15 06:02:39'),(76,20,10,NULL,'2025-10-17 05:09:50'),(77,20,11,NULL,'2025-10-17 05:09:52'),(79,9,NULL,8,'2025-10-18 02:44:36'),(80,9,NULL,9,'2025-10-18 02:44:42'),(83,9,11,NULL,'2025-10-18 10:36:53'),(84,9,10,NULL,'2025-10-18 10:38:50');
/*!40000 ALTER TABLE `tbl_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_otp_verifications`
--

DROP TABLE IF EXISTS `tbl_otp_verifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_otp_verifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `otp` varchar(255) NOT NULL,
  `verified` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_otp_verifications`
--

LOCK TABLES `tbl_otp_verifications` WRITE;
/*!40000 ALTER TABLE `tbl_otp_verifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_otp_verifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_post_media`
--

DROP TABLE IF EXISTS `tbl_post_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_post_media` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `media_url` varchar(255) NOT NULL,
  `media_type` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `tbl_post_media_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `tbl_posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_post_media`
--

LOCK TABLES `tbl_post_media` WRITE;
/*!40000 ALTER TABLE `tbl_post_media` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_post_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_posts`
--

DROP TABLE IF EXISTS `tbl_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text,
  `media_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `is_public` bit(1) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `likes_count` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `FK4724h28qt8lgaubhdh6vqyry1` (`category_id`),
  CONSTRAINT `FK4724h28qt8lgaubhdh6vqyry1` FOREIGN KEY (`category_id`) REFERENCES `tbl_categories` (`id`),
  CONSTRAINT `tbl_posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_posts`
--

LOCK TABLES `tbl_posts` WRITE;
/*!40000 ALTER TABLE `tbl_posts` DISABLE KEYS */;
INSERT INTO `tbl_posts` VALUES (10,8,'Hello sir good morning!',NULL,'2025-10-09 07:22:19','2025-10-18 10:38:50',NULL,_binary '',NULL,5),(11,8,'Hello guys how are you ?🦄',NULL,'2025-10-10 07:25:49','2025-10-18 10:36:53',NULL,_binary '',NULL,5),(13,8,'Hello sir good morning 111!',NULL,'2025-10-10 08:39:07','2025-10-15 03:16:59',NULL,_binary '',NULL,2),(16,18,'hi',NULL,'2025-10-13 03:05:47','2025-10-13 03:05:47',NULL,_binary '',NULL,0),(20,11,'today 15 oct post',NULL,'2025-10-15 02:47:46','2025-10-15 02:48:04',NULL,_binary '',NULL,1),(21,11,'i am good person',NULL,'2025-10-15 03:16:43','2025-10-15 03:16:53',NULL,_binary '',NULL,1),(25,11,'🌸',NULL,'2025-10-15 03:39:49','2025-10-15 03:39:49',NULL,_binary '',NULL,0),(27,9,'dgdfg🎮',NULL,'2025-10-17 01:36:04','2025-10-17 01:36:04',NULL,_binary '',NULL,0),(30,9,'🦄dfgdfg',NULL,'2025-10-17 01:36:17','2025-10-17 01:36:17',NULL,_binary '',NULL,0);
/*!40000 ALTER TABLE `tbl_posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_quiz_questions`
--

DROP TABLE IF EXISTS `tbl_quiz_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_quiz_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint NOT NULL,
  `question` text NOT NULL,
  `option1` varchar(255) NOT NULL,
  `option2` varchar(255) NOT NULL,
  `option3` varchar(255) NOT NULL,
  `option4` varchar(255) NOT NULL,
  `correct_option` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `quiz_id` (`quiz_id`),
  CONSTRAINT `tbl_quiz_questions_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `tbl_quizzes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_quiz_questions`
--

LOCK TABLES `tbl_quiz_questions` WRITE;
/*!40000 ALTER TABLE `tbl_quiz_questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_quiz_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_quizzes`
--

DROP TABLE IF EXISTS `tbl_quizzes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_quizzes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `grade_id` bigint NOT NULL,
  `description` text,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `grade_id` (`grade_id`),
  CONSTRAINT `tbl_quizzes_ibfk_1` FOREIGN KEY (`grade_id`) REFERENCES `tbl_grades` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_quizzes`
--

LOCK TABLES `tbl_quizzes` WRITE;
/*!40000 ALTER TABLE `tbl_quizzes` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_quizzes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_replies`
--

DROP TABLE IF EXISTS `tbl_replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_replies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `reply_text` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comment_id` (`comment_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tbl_replies_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `tbl_comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tbl_replies_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_replies`
--

LOCK TABLES `tbl_replies` WRITE;
/*!40000 ALTER TABLE `tbl_replies` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbl_replies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_roles`
--

DROP TABLE IF EXISTS `tbl_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` enum('USER','MODERATOR','ADMIN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_roles`
--

LOCK TABLES `tbl_roles` WRITE;
/*!40000 ALTER TABLE `tbl_roles` DISABLE KEYS */;
INSERT INTO `tbl_roles` VALUES (1,'USER'),(2,'MODERATOR'),(3,'ADMIN');
/*!40000 ALTER TABLE `tbl_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_tags`
--

DROP TABLE IF EXISTS `tbl_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `usage_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_tags`
--

LOCK TABLES `tbl_tags` WRITE;
/*!40000 ALTER TABLE `tbl_tags` DISABLE KEYS */;
INSERT INTO `tbl_tags` VALUES (1,'fun',NULL,NULL),(2,'tutorial',NULL,NULL),(3,'question',NULL,NULL),(4,'news',NULL,NULL),(5,'emoji',NULL,NULL),(6,'beginner',NULL,NULL),(7,'advanced',NULL,NULL),(8,'discussion',NULL,NULL),(9,'help',NULL,NULL),(10,'showcase',NULL,NULL),(13,'🎵',NULL,0),(14,'🌈',NULL,0),(15,'🔥',NULL,0),(16,'😊',NULL,0),(17,'🎊',NULL,0),(18,'❤',NULL,0),(19,'🎉',NULL,0),(20,'🎨',NULL,0),(21,'🎮',NULL,0),(22,'🌸',NULL,0);
/*!40000 ALTER TABLE `tbl_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_users`
--

DROP TABLE IF EXISTS `tbl_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(250) DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_verified` tinyint(1) DEFAULT '0',
  `role` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `school_name` varchar(255) DEFAULT NULL COMMENT 'Optional field to store the school name of the user',
  `last_seen` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_online` tinyint(1) DEFAULT '0',
  `online_status` varchar(20) DEFAULT 'OFFLINE',
  `dob` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mobile_number` (`mobile_number`),
  UNIQUE KEY `mobile_number_2` (`mobile_number`),
  UNIQUE KEY `UKj562wwmipqt96rkoqbo0jc34` (`email`),
  UNIQUE KEY `UK2fdyon7ywp2axr6w9avheqoqt` (`mobile_number`),
  KEY `idx_users_online_status` (`is_online`,`last_seen`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_users`
--

LOCK TABLES `tbl_users` WRITE;
/*!40000 ALTER TABLE `tbl_users` DISABLE KEYS */;
INSERT INTO `tbl_users` VALUES (2,'Brajesh',10,'Male','Singapore','brajesh.mymailx@gmail.com','6582238251','$2a$10$93QTYfppNJjB2uu0wKL6ZO.Em/1b2Ktyt8fUhjTd6IbvsbFV/sDIW',1,'USER','2025-10-09 01:03:40','2025-10-09 01:03:40.306664',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(3,'Prem Kumar',20,'Male','Singapore','braj@gmail.com','657291889208','$2a$10$sOrulUEznYfl/GhSqtHNgOv1X0hen9GjzKQvoEhb28EGHc3X2DrlK',1,'USER','2025-10-09 01:33:31','2025-10-09 01:33:31.066550',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(4,'John Doe',25,'Male','United States','john.doe@ample.com','9876543210','$2a$10$9hatLvIPSqwAv2RFYiOVr.4OU3TjmZNT0Ptm29fzLq8PXnjARoGXu',1,'USER','2025-10-09 01:47:41','2025-10-09 01:47:40.865469',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(8,'John De',25,'Male','United States','john@ample.com','9876543000','$2a$10$F7rI/OyBa3KjZvV20DnaduUCWpcMmuJ78nWIXKoJgjKcLhB5ewqy2',1,'USER','2025-10-10 02:12:07','2025-10-10 02:12:06.977344',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(9,'Dolly',25,'Female','United States','Dolly@ample.com','9876540000','$2a$10$OqDfF9/jpelMK69rBrpbjelXmTmuCrbvTjf4Nf57HDKxiazGCOEyO',1,'USER','2025-10-10 07:33:33','2025-10-21 08:00:05.168927',_binary '',NULL,'2025-10-21 00:00:05',0,'offline','14/10/1995'),(10,'Divyansh',25,'Male','United States','Divyansh@ample.com','9876000000','$2a$10$2eLha1rBy28e9KQsrx8qSe9EcLIDL7jz.Y1vbRsQf0YHjiFPKGnFS',1,'USER','2025-10-10 08:38:48','2025-10-10 08:38:47.735968',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(11,'Reyansh',8,'Male','Singapore','Reyansh@ample.com','9870000000','$2a$10$5Tg1vFNbwSXxDav2VJziq.MuRblSEg3ocnQdDZY1Me19mqULZxqte',1,'USER','2025-10-11 02:07:46','2025-10-21 07:59:41.486317',_binary '',NULL,'2025-10-20 23:59:41',0,'offline','10/07/2017'),(12,'test',8,'Male','Singapore','Brajesh@ample.com','1123456789','$2a$10$v7.2jS0hsFsPn/vZFlI0w.Koq9uki7vpHxurQJpzvtN28D0eGpL7.',1,'USER','2025-10-11 06:53:31','2025-10-11 06:53:31.284201',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(13,'Brajesh Yadav',9,'Male','Singapore','brajesh.mymaox@gmail.com','6582238253','$2a$10$wvMeptnOTAn.wG5dbBsDwu3QKGiIxmphHjmQgoVCX/azXJiyugtWm',1,'USER','2025-10-11 09:57:02','2025-10-11 09:57:01.881280',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(14,'dggdfg',10,'Male','Singapore','divyansh.ailbox@gmail.com','1000000000','$2a$10$JA59qXqLBJ2Igxk.pEs1L.g1ZTY30seOu4cckipnoMymiS/LmS3Xu',1,'USER','2025-10-11 15:28:30','2025-10-11 15:28:29.652737',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(15,'saurabh',10,'Male','India','sh.mymailbox@gmail.com','9865321472','$2a$10$84zu5K4B1fnlgu2loUQjBOK/iPTYVMpP4NEXfXq4PmZ4VyNUnrBpO',1,'USER','2025-10-12 01:40:27','2025-10-12 01:40:26.552925',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(16,'devid',10,'Male','United Kingdom','brajesh.mox@gmail.com','9988776655','$2a$10$fz185W9czmX9snowU9FJVuVTbi1sQa3fi3zh1y.rYqASoh/SXEgpO',0,'USER','2025-10-12 03:20:19','2025-10-12 03:20:18.959936',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(17,'aaa',10,'Male','Canada','brajesh.mymailbox@gmail.com','8899665577','$2a$10$3jDznKl2tjE61dEZAfiC8OeRoVla.kdKs8sAotytnTYQBtDRO40SK',1,'USER','2025-10-12 04:28:25','2025-10-12 04:30:08.280289',_binary '',NULL,'2025-10-12 13:25:54',0,'OFFLINE',NULL),(18,'admin',40,'Male','India','admin@gmail.com','2255887799','$2a$10$kN9np2DtJiSWtwdUn3NppeZOFTamCSJOi9rS/9rbEJUwmXdZoxCIS',1,'ADMIN','2025-10-13 02:02:18','2025-10-15 07:58:51.691014',_binary '','self','2025-10-14 23:58:51',1,'online',NULL),(19,'fdhfgh',NULL,'Male','United Kingdom','br11aj@gmail.com','5454545432','$2a$10$21geYgme5H.HSWmGC7c3leEedzQuwIkk0l6FICeB.0c0CYqDlkU8S',0,'USER','2025-10-17 03:28:46','2025-10-17 03:28:45.500941',_binary '','gg',NULL,0,'offline',NULL),(20,'Brajesh',NULL,'Male','United States','tech8talk@gmail.com','1122331122','$2a$10$JphKz7C4va0rTeNmB/3VdO6KCMRnaC7gKE9RgIO/UrdmBOuOafqnC',0,'USER','2025-10-17 05:08:14','2025-10-17 05:09:10.323098',_binary '','KKIS',NULL,0,'offline','14/02/2020'),(21,'Dolly Singh',NULL,'Female','Germany','brajesh.cavisa@gmail.com','9966338855','$2a$10$kKwU.ecvRCPLDlr0L5uSA.LXDTk8S/bLx3hiXTUF8gjMikVS0IXOO',1,'TEACHER','2025-10-17 05:14:02','2025-10-17 07:42:26.851938',_binary '','HBTI - Math',NULL,0,'offline','14/10/1985');
/*!40000 ALTER TABLE `tbl_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_conversation_list`
--

DROP TABLE IF EXISTS `v_conversation_list`;
/*!50001 DROP VIEW IF EXISTS `v_conversation_list`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_conversation_list` AS SELECT 
 1 AS `conversation_id`,
 1 AS `user_one_id`,
 1 AS `user_two_id`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `user_one_name`,
 1 AS `user_one_gender`,
 1 AS `user_one_email`,
 1 AS `user_two_name`,
 1 AS `user_two_gender`,
 1 AS `user_two_email`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_friendships`
--

DROP TABLE IF EXISTS `v_friendships`;
/*!50001 DROP VIEW IF EXISTS `v_friendships`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_friendships` AS SELECT 
 1 AS `id`,
 1 AS `user1_id`,
 1 AS `user2_id`,
 1 AS `status`,
 1 AS `requester_id`,
 1 AS `responder_id`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `responded_at`,
 1 AS `user1_name`,
 1 AS `user1_email`,
 1 AS `user1_mobile`,
 1 AS `user2_name`,
 1 AS `user2_email`,
 1 AS `user2_mobile`,
 1 AS `requester_name`,
 1 AS `responder_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'emoji_sphere'
--
/*!50003 DROP FUNCTION IF EXISTS `get_ordered_user_ids` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `get_ordered_user_ids`(
    p_user_id1 BIGINT, 
    p_user_id2 BIGINT
) RETURNS json
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE result JSON;
    
    IF p_user_id1 < p_user_id2 THEN
        SET result = JSON_OBJECT('user1_id', p_user_id1, 'user2_id', p_user_id2);
    ELSE
        SET result = JSON_OBJECT('user1_id', p_user_id2, 'user2_id', p_user_id1);
    END IF;
    
    RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetConversationDetails` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetConversationDetails`(
    IN p_conversation_id BIGINT,
    IN p_current_user_id BIGINT
)
BEGIN
    SELECT 
        c.id,
        c.user_one_id,
        c.user_two_id,
        c.created_at,
        c.updated_at,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN c.user_two_id
            ELSE c.user_one_id
        END as other_user_id,
        u1.full_name as user_one_name,
        u1.gender as user_one_gender,
        u2.full_name as user_two_name,
        u2.gender as user_two_gender,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN u2.full_name
            ELSE u1.full_name
        END as other_user_name,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN u2.gender
            ELSE u1.gender
        END as other_user_gender,
        cs.notifications_enabled,
        cs.archived,
        cs.muted_until,
        -- Count unread messages
        (SELECT COUNT(*) 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         AND m.receiver_id = p_current_user_id 
         AND m.is_read = 0) as unread_count,
        -- Get last message
        (SELECT m.message_text 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         ORDER BY m.created_at DESC 
         LIMIT 1) as last_message,
        -- Get last message time
        (SELECT m.created_at 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         ORDER BY m.created_at DESC 
         LIMIT 1) as last_message_time
    FROM tbl_chat_conversations c
    LEFT JOIN tbl_users u1 ON c.user_one_id = u1.id
    LEFT JOIN tbl_users u2 ON c.user_two_id = u2.id
    LEFT JOIN tbl_conversation_settings cs ON c.id = cs.conversation_id AND cs.user_id = p_current_user_id
    WHERE c.id = p_conversation_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetOrCreateConversation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetOrCreateConversation`(
    IN p_user1_id BIGINT,
    IN p_user2_id BIGINT
)
BEGIN
    DECLARE v_conversation_id BIGINT DEFAULT NULL;
    DECLARE v_user_one_id BIGINT;
    DECLARE v_user_two_id BIGINT;
    
    -- Order user IDs consistently (smaller first)
    IF p_user1_id < p_user2_id THEN
        SET v_user_one_id = p_user1_id;
        SET v_user_two_id = p_user2_id;
    ELSE
        SET v_user_one_id = p_user2_id;
        SET v_user_two_id = p_user1_id;
    END IF;
    
    -- Try to find existing conversation
    SELECT id INTO v_conversation_id
    FROM tbl_chat_conversations
    WHERE user_one_id = v_user_one_id AND user_two_id = v_user_two_id;
    
    -- Create conversation if it doesn't exist
    IF v_conversation_id IS NULL THEN
        INSERT INTO tbl_chat_conversations (user_one_id, user_two_id)
        VALUES (v_user_one_id, v_user_two_id);
        
        SET v_conversation_id = LAST_INSERT_ID();
        
        -- Create default settings for both users
        INSERT INTO tbl_conversation_settings (conversation_id, user_id)
        VALUES 
            (v_conversation_id, v_user_one_id),
            (v_conversation_id, v_user_two_id);
    END IF;
    
    SELECT v_conversation_id as conversation_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `MarkMessagesAsRead` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `MarkMessagesAsRead`(
    IN p_conversation_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
    UPDATE tbl_chat_messages 
    SET is_read = 1, updated_at = CURRENT_TIMESTAMP
    WHERE conversation_id = p_conversation_id 
    AND receiver_id = p_user_id 
    AND is_read = 0;
    
    -- Update message status table
    INSERT INTO tbl_chat_message_status (message_id, read_at)
    SELECT m.id, CURRENT_TIMESTAMP
    FROM tbl_chat_messages m
    WHERE m.conversation_id = p_conversation_id 
    AND m.receiver_id = p_user_id 
    AND m.is_read = 1
    ON DUPLICATE KEY UPDATE read_at = CURRENT_TIMESTAMP;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `respond_to_friend_request` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `respond_to_friend_request`(
    IN p_friendship_id BIGINT,
    IN p_responder_id BIGINT,
    IN p_response ENUM('ACCEPTED', 'DECLINED', 'BLOCKED'),
    OUT p_result VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_requester_id BIGINT;
    DECLARE v_user1_id BIGINT;
    DECLARE v_user2_id BIGINT;
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_valid_responder INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR: Database error occurred';
    END;
    
    START TRANSACTION;
    
    -- Get friendship details
    SELECT user1_id, user2_id, requester_id, status 
    INTO v_user1_id, v_user2_id, v_requester_id, v_current_status
    FROM tbl_friendships 
    WHERE id = p_friendship_id;
    
    -- Check if friendship exists
    IF v_requester_id IS NULL THEN
        SET p_result = 'ERROR: Friendship request not found';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Check if request is still pending
    IF v_current_status != 'PENDING' THEN
        SET p_result = 'ERROR: Friendship request is no longer pending';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Validate responder (must be the other user, not the requester)
    IF (p_responder_id = v_user1_id AND p_responder_id != v_requester_id) OR 
       (p_responder_id = v_user2_id AND p_responder_id != v_requester_id) THEN
        SET v_valid_responder = 1;
    END IF;
    
    IF v_valid_responder = 0 THEN
        SET p_result = 'ERROR: Invalid responder for this friendship request';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Update friendship status
    UPDATE tbl_friendships 
    SET status = p_response,
        responder_id = p_responder_id,
        responded_at = CURRENT_TIMESTAMP
    WHERE id = p_friendship_id;
    
    SET p_result = CONCAT('SUCCESS: Friendship request ', LOWER(p_response));
    
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `send_friend_request` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `send_friend_request`(
    IN p_requester_id BIGINT,
    IN p_target_user_id BIGINT,
    OUT p_result VARCHAR(255),
    OUT p_friendship_id BIGINT
)
proc_label: BEGIN
    DECLARE v_user1_id BIGINT;
    DECLARE v_user2_id BIGINT;
    DECLARE v_existing_count INT DEFAULT 0;
    DECLARE v_requester_exists INT DEFAULT 0;
    DECLARE v_target_exists INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR: Database error occurred';
        SET p_friendship_id = NULL;
    END;
    
    START TRANSACTION;
    
    -- Validate users exist
    SELECT COUNT(*) INTO v_requester_exists FROM tbl_users WHERE id = p_requester_id AND is_active = 1;
    SELECT COUNT(*) INTO v_target_exists FROM tbl_users WHERE id = p_target_user_id AND is_active = 1;
    
    IF v_requester_exists = 0 THEN
        SET p_result = 'ERROR: Requester user not found or inactive';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    IF v_target_exists = 0 THEN
        SET p_result = 'ERROR: Target user not found or inactive';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Cannot send friend request to yourself
    IF p_requester_id = p_target_user_id THEN
        SET p_result = 'ERROR: Cannot send friend request to yourself';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Get ordered user IDs
    IF p_requester_id < p_target_user_id THEN
        SET v_user1_id = p_requester_id;
        SET v_user2_id = p_target_user_id;
    ELSE
        SET v_user1_id = p_target_user_id;
        SET v_user2_id = p_requester_id;
    END IF;
    
    -- Check if friendship already exists
    SELECT COUNT(*) INTO v_existing_count 
    FROM tbl_friendships 
    WHERE user1_id = v_user1_id AND user2_id = v_user2_id;
    
    IF v_existing_count > 0 THEN
        SET p_result = 'ERROR: Friendship relationship already exists';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Create friendship request
    INSERT INTO tbl_friendships (user1_id, user2_id, status, requester_id)
    VALUES (v_user1_id, v_user2_id, 'PENDING', p_requester_id);
    
    SET p_friendship_id = LAST_INSERT_ID();
    SET p_result = 'SUCCESS: Friend request sent successfully';
    
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_get_group_posts_with_details_json` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_group_posts_with_details_json`(
    IN p_group_id BIGINT,
    IN p_offset INT,
    IN p_limit INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
        SELECT CONCAT('ERROR: ', @p1, ' - ', @p2) as error_message;
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'post_id', p.id,
            'user_id', p.user_id,
            'user_name', COALESCE(u.full_name, 'Unknown User'),
            'content', COALESCE(p.content, ''),
            'media_url', p.media_url,
            'created_at', DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s'),
            'updated_at', DATE_FORMAT(COALESCE(p.updated_at, p.created_at), '%Y-%m-%d %H:%i:%s'),
            'like_count', COALESCE(p.likes_count, 0),
            'comment_count', COALESCE(comment_counts.comment_count, 0),
            'media', COALESCE(post_media.media_json, JSON_ARRAY())
        )
    ) AS posts_json
    FROM tbl_group_posts p
    LEFT JOIN tbl_users u ON p.user_id = u.id
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(*) as comment_count
        FROM tbl_group_comments 
        WHERE parent_comment_id IS NULL
        GROUP BY post_id
    ) comment_counts ON p.id = comment_counts.post_id
    LEFT JOIN (
        SELECT 
            m.post_id,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'media_id', m.id,
                    'media_url', m.media_url,
                    'media_type', m.media_type,
                    'created_at', DATE_FORMAT(m.created_at, '%Y-%m-%d %H:%i:%s')
                )
            ) as media_json
        FROM tbl_group_post_media m
        GROUP BY m.post_id
    ) post_media ON p.id = post_media.post_id
    WHERE p.group_id = p_group_id
    ORDER BY p.created_at DESC
    LIMIT p_limit OFFSET p_offset;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_get_posts_with_details_json` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_posts_with_details_json`(
    IN p_offset INT,
    IN p_limit INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
        SELECT CONCAT('ERROR: ', @p1, ' - ', @p2) as error_message;
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'post_id', p.id,
            'user_id', p.user_id,
            'user_name', COALESCE(u.full_name, 'Unknown User'),
            'gender', COALESCE(u.gender, 'Unknown'),
            'country', COALESCE(u.country, 'Unknown'),
            'content', COALESCE(p.content, ''),
            'media_url', p.media_url,
            'created_at', DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s'),
            'updated_at', DATE_FORMAT(COALESCE(p.updated_at, p.created_at), '%Y-%m-%d %H:%i:%s'),
            'like_count', COALESCE(p.likes_count, 0),
            'comment_count', COALESCE(comment_counts.comment_count, 0),
            'comments', COALESCE(post_comments.comments_json, JSON_ARRAY())
        )
    ) AS posts_json
    FROM tbl_posts p
    LEFT JOIN tbl_users u ON p.user_id = u.id
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(*) as comment_count
        FROM tbl_comments 
        WHERE parent_comment_id IS NULL
        GROUP BY post_id
    ) comment_counts ON p.id = comment_counts.post_id
    LEFT JOIN (
        SELECT 
            c.post_id,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'comment_id', c.id,
                    'comment_text', COALESCE(c.comment_text, ''),
                    'commented_by', COALESCE(cu.full_name, 'Unknown User'),
                    'comment_created_at', DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i:%s'),
                    'like_count', COALESCE(cl.like_count, 0),
                    'replies', COALESCE(cr.replies_json, JSON_ARRAY())
                )
            ) as comments_json
        FROM tbl_comments c
        LEFT JOIN tbl_users cu ON c.user_id = cu.id
        LEFT JOIN (
            SELECT 
                comment_id, 
                COUNT(*) as like_count
            FROM tbl_likes 
            WHERE comment_id IS NOT NULL
            GROUP BY comment_id
        ) cl ON c.id = cl.comment_id
        LEFT JOIN (
            SELECT 
                r.parent_comment_id,
                JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'reply_id', r.id,
                        'reply_text', COALESCE(r.comment_text, ''),
                        'replied_by', COALESCE(ru.full_name, 'Unknown User'),
                        'reply_created_at', DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s')
                    )
                ) as replies_json
            FROM tbl_comments r
            LEFT JOIN tbl_users ru ON r.user_id = ru.id
            WHERE r.parent_comment_id IS NOT NULL
            GROUP BY r.parent_comment_id
        ) cr ON c.id = cr.parent_comment_id
        WHERE c.parent_comment_id IS NULL
        GROUP BY c.post_id
    ) post_comments ON p.id = post_comments.post_id
    WHERE p.is_public = 1
    ORDER BY p.created_at DESC
    LIMIT p_limit OFFSET p_offset;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_get_posts_with_details_json1` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_posts_with_details_json1`(
    IN p_offset INT,
    IN p_limit INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
        SELECT CONCAT('ERROR: ', @p1, ' - ', @p2) as error_message;
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'post_id', p.id,
            'user_id', p.user_id,
            'user_name', COALESCE(u.full_name, 'Unknown User'),
            'gender', COALESCE(u.gender, 'Unknown'),
            'country', COALESCE(u.country, 'Unknown'),
            'content', COALESCE(p.content, ''),
            'media_url', p.media_url,
            'created_at', DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s'),
            'updated_at', DATE_FORMAT(COALESCE(p.updated_at, p.created_at), '%Y-%m-%d %H:%i:%s'),
            'like_count', COALESCE(p.likes_count, 0),
            'comment_count', COALESCE(comment_counts.comment_count, 0),
            'comments', COALESCE(post_comments.comments_json, JSON_ARRAY())
        )
    ) AS posts_json
    FROM tbl_posts p
    LEFT JOIN tbl_users u ON p.user_id = u.id
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(*) as comment_count
        FROM tbl_comments 
        WHERE parent_comment_id IS NULL
        GROUP BY post_id
    ) comment_counts ON p.id = comment_counts.post_id
    LEFT JOIN (
        SELECT 
            c.post_id,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'comment_id', c.id,
                    'comment_text', COALESCE(c.comment_text, ''),
                    'commented_by', COALESCE(cu.full_name, 'Unknown User'),
                    'comment_created_at', DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i:%s'),
                    'like_count', COALESCE(cl.like_count, 0),
                    'replies', COALESCE(cr.replies_json, JSON_ARRAY())
                )
            ) as comments_json
        FROM tbl_comments c
        LEFT JOIN tbl_users cu ON c.user_id = cu.id
        LEFT JOIN (
            SELECT 
                comment_id, 
                COUNT(*) as like_count
            FROM tbl_likes 
            WHERE comment_id IS NOT NULL
            GROUP BY comment_id
        ) cl ON c.id = cl.comment_id
        LEFT JOIN (
            SELECT 
                r.parent_comment_id,
                JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'reply_id', r.id,
                        'reply_text', COALESCE(r.comment_text, ''),
                        'replied_by', COALESCE(ru.full_name, 'Unknown User'),
                        'reply_created_at', DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s')
                    )
                ) as replies_json
            FROM tbl_comments r
            LEFT JOIN tbl_users ru ON r.user_id = ru.id
            WHERE r.parent_comment_id IS NOT NULL
            GROUP BY r.parent_comment_id
        ) cr ON c.id = cr.parent_comment_id
        WHERE c.parent_comment_id IS NULL
        GROUP BY c.post_id
    ) post_comments ON p.id = post_comments.post_id
    WHERE p.is_public = 1
    ORDER BY p.created_at DESC
    LIMIT p_limit OFFSET p_offset;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_get_user_conversations` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_user_conversations`(IN p_user_id BIGINT)
BEGIN
    SELECT 
        c.id AS conversation_id,
        u.id AS friend_id,
        u.full_name AS display_name,
        u.avatar_url,
        m.message_text AS last_message,
        m.created_at AS last_message_time
    FROM tbl_conversations c
    JOIN tbl_users u 
      ON u.id = CASE 
          WHEN c.user_one_id = p_user_id THEN c.user_two_id
          ELSE c.user_one_id
      END
    LEFT JOIN (
        SELECT t1.*
        FROM tbl_chat_messages t1
        INNER JOIN (
            SELECT conversation_id, MAX(created_at) AS max_time
            FROM tbl_chat_messages
            GROUP BY conversation_id
        ) t2 ON t1.conversation_id = t2.conversation_id AND t1.created_at = t2.max_time
    ) m ON m.conversation_id = c.id
    WHERE c.user_one_id = p_user_id OR c.user_two_id = p_user_id
    ORDER BY m.created_at DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `v_conversation_list`
--

/*!50001 DROP VIEW IF EXISTS `v_conversation_list`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_conversation_list` AS select `c`.`id` AS `conversation_id`,`c`.`user_one_id` AS `user_one_id`,`c`.`user_two_id` AS `user_two_id`,`c`.`created_at` AS `created_at`,`c`.`updated_at` AS `updated_at`,`u1`.`full_name` AS `user_one_name`,`u1`.`gender` AS `user_one_gender`,`u1`.`email` AS `user_one_email`,`u2`.`full_name` AS `user_two_name`,`u2`.`gender` AS `user_two_gender`,`u2`.`email` AS `user_two_email` from ((`tbl_chat_conversations` `c` left join `tbl_users` `u1` on((`c`.`user_one_id` = `u1`.`id`))) left join `tbl_users` `u2` on((`c`.`user_two_id` = `u2`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_friendships`
--

/*!50001 DROP VIEW IF EXISTS `v_friendships`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_friendships` AS select `f`.`id` AS `id`,`f`.`user1_id` AS `user1_id`,`f`.`user2_id` AS `user2_id`,`f`.`status` AS `status`,`f`.`requester_id` AS `requester_id`,`f`.`responder_id` AS `responder_id`,`f`.`created_at` AS `created_at`,`f`.`updated_at` AS `updated_at`,`f`.`responded_at` AS `responded_at`,`u1`.`full_name` AS `user1_name`,`u1`.`email` AS `user1_email`,`u1`.`mobile_number` AS `user1_mobile`,`u2`.`full_name` AS `user2_name`,`u2`.`email` AS `user2_email`,`u2`.`mobile_number` AS `user2_mobile`,`ur`.`full_name` AS `requester_name`,(case when (`f`.`responder_id` is not null) then `urs`.`full_name` else NULL end) AS `responder_name` from ((((`tbl_friendships` `f` join `tbl_users` `u1` on((`f`.`user1_id` = `u1`.`id`))) join `tbl_users` `u2` on((`f`.`user2_id` = `u2`.`id`))) join `tbl_users` `ur` on((`f`.`requester_id` = `ur`.`id`))) left join `tbl_users` `urs` on((`f`.`responder_id` = `urs`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-06 16:32:17
