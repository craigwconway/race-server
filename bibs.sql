-- MySQL dump 10.13  Distrib 5.6.20, for osx10.7 (x86_64)
--
-- Host: localhost    Database: bibs
-- ------------------------------------------------------
-- Server version	5.6.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `award_category`
--

DROP TABLE IF EXISTS `award_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `award_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age_max` int(11) NOT NULL,
  `age_min` int(11) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `list_size` int(11) NOT NULL,
  `master` bit(1) NOT NULL,
  `medal` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sort_order` int(11) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_87dn6h5jipem1q3acyagvddc6` (`event`),
  CONSTRAINT `FK_87dn6h5jipem1q3acyagvddc6` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `award_category`
--

LOCK TABLES `award_category` WRITE;
/*!40000 ALTER TABLE `award_category` DISABLE KEYS */;
INSERT INTO `award_category` VALUES (151,109,0,'M',3,'\0','\0','Medal: Top Males Overall',1,0,4),(152,109,0,'F',3,'\0','\0','Medal: Top Females Overall',2,0,4),(153,49,40,'M',3,'\0','\0','Medal: Top Male Masters',3,0,4),(154,49,40,'F',3,'\0','\0','Medal: Top Female Masters',4,0,4),(155,109,50,'M',3,'\0','\0','Medal: Top Male Grand Masters',5,0,4),(156,109,50,'F',3,'\0','\0','Medal: Top Female Grand Masters',6,0,4),(157,4,0,'M',3,'\0','\0','Male Ages 0 to 4',1,0,4),(158,4,0,'F',3,'\0','\0','Female Ages 0 to 4',2,0,4),(159,9,5,'M',3,'\0','\0','Male Ages 5 to 9',3,0,4),(160,9,5,'F',3,'\0','\0','Female Ages 5 to 9',4,0,4),(161,14,10,'M',3,'\0','\0','Male Ages 10 to 14',5,0,4),(162,14,10,'F',3,'\0','\0','Female Ages 10 to 14',6,0,4),(163,19,15,'M',3,'\0','\0','Male Ages 15 to 19',7,0,4),(164,19,15,'F',3,'\0','\0','Female Ages 15 to 19',8,0,4),(165,24,20,'M',3,'\0','\0','Male Ages 20 to 24',9,0,4),(166,24,20,'F',3,'\0','\0','Female Ages 20 to 24',10,0,4),(167,29,25,'M',3,'\0','\0','Male Ages 25 to 29',11,0,4),(168,29,25,'F',3,'\0','\0','Female Ages 25 to 29',12,0,4),(169,34,30,'M',3,'\0','\0','Male Ages 30 to 34',13,0,4),(170,34,30,'F',3,'\0','\0','Female Ages 30 to 34',14,0,4),(171,39,35,'M',3,'\0','\0','Male Ages 35 to 39',15,0,4),(172,39,35,'F',3,'\0','\0','Female Ages 35 to 39',16,0,4),(173,44,40,'M',3,'\0','\0','Male Ages 40 to 44',17,0,4),(174,44,40,'F',3,'\0','\0','Female Ages 40 to 44',18,0,4),(175,49,45,'M',3,'\0','\0','Male Ages 45 to 49',19,0,4),(176,49,45,'F',3,'\0','\0','Female Ages 45 to 49',20,0,4),(177,54,50,'M',3,'\0','\0','Male Ages 50 to 54',21,0,4),(178,54,50,'F',3,'\0','\0','Female Ages 50 to 54',22,0,4),(179,59,55,'M',3,'\0','\0','Male Ages 55 to 59',23,0,4),(180,59,55,'F',3,'\0','\0','Female Ages 55 to 59',24,0,4),(181,64,60,'M',3,'\0','\0','Male Ages 60 to 64',25,0,4),(182,64,60,'F',3,'\0','\0','Female Ages 60 to 64',26,0,4),(183,69,65,'M',3,'\0','\0','Male Ages 65 to 69',27,0,4),(184,69,65,'F',3,'\0','\0','Female Ages 65 to 69',28,0,4),(185,74,70,'M',3,'\0','\0','Male Ages 70 to 74',29,0,4),(186,74,70,'F',3,'\0','\0','Female Ages 70 to 74',30,0,4),(187,79,75,'M',3,'\0','\0','Male Ages 75 to 79',31,0,4),(188,79,75,'F',3,'\0','\0','Female Ages 75 to 79',32,0,4),(189,84,80,'M',3,'\0','\0','Male Ages 80 to 84',33,0,4),(190,84,80,'F',3,'\0','\0','Female Ages 80 to 84',34,0,4),(191,89,85,'M',3,'\0','\0','Male Ages 85 to 89',35,0,4),(192,89,85,'F',3,'\0','\0','Female Ages 85 to 89',36,0,4),(193,94,90,'M',3,'\0','\0','Male Ages 90 to 94',37,0,4),(194,94,90,'F',3,'\0','\0','Female Ages 90 to 94',38,0,4),(195,99,95,'M',3,'\0','\0','Male Ages 95 to 99',39,0,4),(196,99,95,'F',3,'\0','\0','Female Ages 95 to 99',40,0,4),(197,104,100,'M',3,'\0','\0','Male Ages 100 to 104',41,0,4),(198,104,100,'F',3,'\0','\0','Female Ages 100 to 104',42,0,4),(199,109,105,'M',3,'\0','\0','Male Ages 105 to 109',43,0,4),(200,109,105,'F',3,'\0','\0','Female Ages 105 to 109',44,0,4),(201,109,0,'M',3,'\0','\0','Medal: Top Males Overall',1,0,5),(202,109,0,'F',3,'\0','\0','Medal: Top Females Overall',2,0,5),(203,49,40,'M',3,'\0','\0','Medal: Top Male Masters',3,0,5),(204,49,40,'F',3,'\0','\0','Medal: Top Female Masters',4,0,5),(205,109,50,'M',3,'\0','\0','Medal: Top Male Grand Masters',5,0,5),(206,109,50,'F',3,'\0','\0','Medal: Top Female Grand Masters',6,0,5),(207,4,0,'M',3,'\0','\0','Male Ages 0 to 4',1,0,5),(208,4,0,'F',3,'\0','\0','Female Ages 0 to 4',2,0,5),(209,9,5,'M',3,'\0','\0','Male Ages 5 to 9',3,0,5),(210,9,5,'F',3,'\0','\0','Female Ages 5 to 9',4,0,5),(211,14,10,'M',3,'\0','\0','Male Ages 10 to 14',5,0,5),(212,14,10,'F',3,'\0','\0','Female Ages 10 to 14',6,0,5),(213,19,15,'M',3,'\0','\0','Male Ages 15 to 19',7,0,5),(214,19,15,'F',3,'\0','\0','Female Ages 15 to 19',8,0,5),(215,24,20,'M',3,'\0','\0','Male Ages 20 to 24',9,0,5),(216,24,20,'F',3,'\0','\0','Female Ages 20 to 24',10,0,5),(217,29,25,'M',3,'\0','\0','Male Ages 25 to 29',11,0,5),(218,29,25,'F',3,'\0','\0','Female Ages 25 to 29',12,0,5),(219,34,30,'M',3,'\0','\0','Male Ages 30 to 34',13,0,5),(220,34,30,'F',3,'\0','\0','Female Ages 30 to 34',14,0,5),(221,39,35,'M',3,'\0','\0','Male Ages 35 to 39',15,0,5),(222,39,35,'F',3,'\0','\0','Female Ages 35 to 39',16,0,5),(223,44,40,'M',3,'\0','\0','Male Ages 40 to 44',17,0,5),(224,44,40,'F',3,'\0','\0','Female Ages 40 to 44',18,0,5),(225,49,45,'M',3,'\0','\0','Male Ages 45 to 49',19,0,5),(226,49,45,'F',3,'\0','\0','Female Ages 45 to 49',20,0,5),(227,54,50,'M',3,'\0','\0','Male Ages 50 to 54',21,0,5),(228,54,50,'F',3,'\0','\0','Female Ages 50 to 54',22,0,5),(229,59,55,'M',3,'\0','\0','Male Ages 55 to 59',23,0,5),(230,59,55,'F',3,'\0','\0','Female Ages 55 to 59',24,0,5),(231,64,60,'M',3,'\0','\0','Male Ages 60 to 64',25,0,5),(232,64,60,'F',3,'\0','\0','Female Ages 60 to 64',26,0,5),(233,69,65,'M',3,'\0','\0','Male Ages 65 to 69',27,0,5),(234,69,65,'F',3,'\0','\0','Female Ages 65 to 69',28,0,5),(235,74,70,'M',3,'\0','\0','Male Ages 70 to 74',29,0,5),(236,74,70,'F',3,'\0','\0','Female Ages 70 to 74',30,0,5),(237,79,75,'M',3,'\0','\0','Male Ages 75 to 79',31,0,5),(238,79,75,'F',3,'\0','\0','Female Ages 75 to 79',32,0,5),(239,84,80,'M',3,'\0','\0','Male Ages 80 to 84',33,0,5),(240,84,80,'F',3,'\0','\0','Female Ages 80 to 84',34,0,5),(241,89,85,'M',3,'\0','\0','Male Ages 85 to 89',35,0,5),(242,89,85,'F',3,'\0','\0','Female Ages 85 to 89',36,0,5),(243,94,90,'M',3,'\0','\0','Male Ages 90 to 94',37,0,5),(244,94,90,'F',3,'\0','\0','Female Ages 90 to 94',38,0,5),(245,99,95,'M',3,'\0','\0','Male Ages 95 to 99',39,0,5),(246,99,95,'F',3,'\0','\0','Female Ages 95 to 99',40,0,5),(247,104,100,'M',3,'\0','\0','Male Ages 100 to 104',41,0,5),(248,104,100,'F',3,'\0','\0','Female Ages 100 to 104',42,0,5),(249,109,105,'M',3,'\0','\0','Male Ages 105 to 109',43,0,5),(250,109,105,'F',3,'\0','\0','Female Ages 105 to 109',44,0,5);
/*!40000 ALTER TABLE `award_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `status` int(11) NOT NULL,
  `stripe_charge_id` varchar(255) DEFAULT NULL,
  `stripe_refund_id` varchar(255) DEFAULT NULL,
  `timeout` int(11) NOT NULL,
  `total` bigint(20) NOT NULL,
  `total_pre_fee` bigint(20) NOT NULL,
  `updated` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `coupon` bigint(20) DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ejcqkjh8849ixu1fl9s2ia2sb` (`coupon`),
  KEY `FK_l6au25441wxkhodft85452tck` (`user`),
  CONSTRAINT `FK_ejcqkjh8849ixu1fl9s2ia2sb` FOREIGN KEY (`coupon`) REFERENCES `event_cart_item_coupon` (`id`),
  CONSTRAINT `FK_l6au25441wxkhodft85452tck` FOREIGN KEY (`user`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,'2015-06-19 00:23:43',3,'ch_16FJrWLGyFUL6XkdJLoOIry4','re_16FKSFLGyFUL6XkdFGyYCqYw',600,10700,10000,'2015-06-19 00:23:43',7,NULL,6),(2,NULL,0,NULL,NULL,0,0,0,NULL,0,NULL,NULL),(3,'2015-06-19 13:27:18',3,'ch_16FW6rLGyFUL6Xkdv5MfcYzE',NULL,600,25116,23600,'2015-06-19 13:27:18',7,1,10),(4,NULL,0,NULL,NULL,0,0,0,NULL,0,NULL,NULL),(5,'2015-06-19 13:39:05',3,'ch_16FWHgLGyFUL6XkdUoBaBy3Q',NULL,600,33066,31100,'2015-06-19 13:39:05',7,1,14),(6,NULL,0,NULL,NULL,0,0,0,NULL,0,NULL,NULL),(7,'2015-06-19 14:05:41',3,'ch_16FWhuLGyFUL6Xkd3Q750Tzm',NULL,600,165248,155800,'2015-06-19 14:05:41',7,1,18),(8,NULL,0,NULL,NULL,0,0,0,NULL,0,NULL,NULL);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bib` bigint(20) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `exported` bit(1) DEFAULT NULL,
  `price` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `size` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `cart` bigint(20) DEFAULT NULL,
  `event_cart_item` bigint(20) DEFAULT NULL,
  `event_type` bigint(20) DEFAULT NULL,
  `price_change` bigint(20) DEFAULT NULL,
  `team` bigint(20) DEFAULT NULL,
  `user_profile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_r2jecc1qs002c2t8hrw3gtvtn` (`cart`),
  KEY `FK_905tip8pcdcuxk8qgyrj7a1g4` (`event_cart_item`),
  KEY `FK_c5fxpsubkdvxvksokpndvvdp4` (`event_type`),
  KEY `FK_lvsocujonug8kv6rs0sfhqk18` (`price_change`),
  KEY `FK_d15a2nco9s23tc7w1l9af3xq` (`team`),
  KEY `FK_o5pmdcc4e11c7fwmm9hgekfwq` (`user_profile_id`),
  CONSTRAINT `FK_905tip8pcdcuxk8qgyrj7a1g4` FOREIGN KEY (`event_cart_item`) REFERENCES `event_cart_item` (`id`),
  CONSTRAINT `FK_c5fxpsubkdvxvksokpndvvdp4` FOREIGN KEY (`event_type`) REFERENCES `event_type` (`id`),
  CONSTRAINT `FK_d15a2nco9s23tc7w1l9af3xq` FOREIGN KEY (`team`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_lvsocujonug8kv6rs0sfhqk18` FOREIGN KEY (`price_change`) REFERENCES `event_cart_item_price_change` (`id`),
  CONSTRAINT `FK_o5pmdcc4e11c7fwmm9hgekfwq` FOREIGN KEY (`user_profile_id`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK_r2jecc1qs002c2t8hrw3gtvtn` FOREIGN KEY (`cart`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (1,NULL,NULL,NULL,'2015-06-19 00:23:43','',10000,1,NULL,'2015-06-19 00:23:43',2,1,1,1,NULL,NULL,6),(2,NULL,NULL,NULL,'2015-06-19 13:27:18',NULL,10000,1,NULL,'2015-06-19 13:27:18',1,3,3,3,NULL,NULL,10),(3,NULL,'White',NULL,'2015-06-19 13:28:10',NULL,10000,1,'M','2015-06-19 13:28:10',1,3,4,NULL,NULL,NULL,10),(4,NULL,NULL,NULL,'2015-06-19 13:28:18',NULL,100,50,NULL,'2015-06-19 13:28:18',1,3,5,NULL,NULL,NULL,10),(5,NULL,NULL,NULL,'2015-06-19 13:39:05',NULL,10000,1,NULL,'2015-06-19 13:39:05',1,5,3,3,NULL,NULL,14),(6,NULL,'Red',NULL,'2015-06-19 13:39:39',NULL,10000,1,'S','2015-06-19 13:39:39',1,5,4,NULL,NULL,NULL,14),(7,NULL,NULL,NULL,'2015-06-19 13:39:49',NULL,100,125,NULL,'2015-06-19 13:39:49',1,5,5,NULL,NULL,NULL,14),(8,NULL,NULL,NULL,'2015-06-19 14:05:41',NULL,10000,1,NULL,'2015-06-19 14:05:41',1,7,3,3,NULL,NULL,18),(9,NULL,'White',NULL,'2015-06-19 14:06:22',NULL,10000,5,'S','2015-06-19 14:06:22',1,7,4,NULL,NULL,NULL,18),(10,NULL,NULL,NULL,'2015-06-19 14:06:37',NULL,100,1000,NULL,'2015-06-19 14:06:37',1,7,5,NULL,NULL,NULL,18);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_reg_field`
--

DROP TABLE IF EXISTS `custom_reg_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_reg_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question` varchar(255) NOT NULL,
  `response_set` varchar(255) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p8ensk4fa86tgaa8bcmwqvecu` (`event`),
  CONSTRAINT `FK_p8ensk4fa86tgaa8bcmwqvecu` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_reg_field`
--

LOCK TABLES `custom_reg_field` WRITE;
/*!40000 ALTER TABLE `custom_reg_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_reg_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_reg_field_response`
--

DROP TABLE IF EXISTS `custom_reg_field_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_reg_field_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `response` varchar(255) DEFAULT NULL,
  `cart` bigint(20) DEFAULT NULL,
  `custom_reg_field` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7w61419m7lb4p3l350wbisrn` (`cart`),
  KEY `FK_hdp7gud73rb5p8j0vp5w7nbf0` (`custom_reg_field`),
  CONSTRAINT `FK_7w61419m7lb4p3l350wbisrn` FOREIGN KEY (`cart`) REFERENCES `cart` (`id`),
  CONSTRAINT `FK_hdp7gud73rb5p8j0vp5w7nbf0` FOREIGN KEY (`custom_reg_field`) REFERENCES `custom_reg_field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_reg_field_response`
--

LOCK TABLES `custom_reg_field_response` WRITE;
/*!40000 ALTER TABLE `custom_reg_field_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_reg_field_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_info`
--

DROP TABLE IF EXISTS `device_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `runners_used` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_info`
--

LOCK TABLES `device_info` WRITE;
/*!40000 ALTER TABLE `device_info` DISABLE KEYS */;
INSERT INTO `device_info` VALUES (1,0);
/*!40000 ALTER TABLE `device_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `allow_masters_in_non_masters` bit(1) NOT NULL,
  `allow_medals_in_age_gender_rankings` bit(1) NOT NULL,
  `charity` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `course_rules` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `donate_url` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `facebook_url1` varchar(255) DEFAULT NULL,
  `featured` int(11) NOT NULL,
  `general` varchar(255) DEFAULT NULL,
  `gun_fired` bit(1) NOT NULL,
  `gun_time` datetime DEFAULT NULL,
  `gun_time_start` bigint(20) NOT NULL,
  `hashtag` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `live` bit(1) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `organization` varchar(255) DEFAULT NULL,
  `parking` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `photo2` varchar(255) DEFAULT NULL,
  `photo3` varchar(255) DEFAULT NULL,
  `photo_upload_url` varchar(255) DEFAULT NULL,
  `absolute_fee` bigint(20) NOT NULL,
  `currency` int(11) DEFAULT NULL,
  `relative_fee` double NOT NULL,
  `reg_enabled` bit(1) NOT NULL,
  `reg_end` datetime DEFAULT NULL,
  `reg_start` datetime DEFAULT NULL,
  `registration` varchar(255) DEFAULT NULL,
  `running` int(11) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `sync` bit(1) NOT NULL,
  `sync_id` varchar(255) DEFAULT NULL,
  `ticket_transfer_cutoff` datetime DEFAULT NULL,
  `ticket_transfer_cutoff_local` varchar(255) DEFAULT NULL,
  `ticket_transfer_enabled` bit(1) NOT NULL,
  `time_start` datetime DEFAULT NULL,
  `time_start_local` varchar(255) DEFAULT NULL,
  `timezone` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `waiver` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (4,'','','\0','','','','','2015-06-19 11:41:36','','','','',0,'','\0',NULL,0,'TimezoneTestEvent',NULL,'\0',NULL,NULL,'Timezone Test Event','','','1231231234','','','','',100,0,0.06,'',NULL,NULL,'',0,'','\0','',NULL,NULL,'\0','2015-06-18 21:00:00','06/19/2015 12:00:00 am','America/New_York','2015-06-19 11:41:45',1,'https://s3-us-west-2.amazonaws.com/galen-shennanigans-2/standard-waiver.txt','http://www.ny.com',''),(5,'','','\0','','','','',NULL,'','','','',0,'','\0',NULL,0,NULL,33.9516114,'',NULL,-118.38757750000002,'eci test','bibs','','1231231234','','','','',100,0,0.06,'',NULL,NULL,'',0,'','\0','',NULL,NULL,'\0','2015-06-23 03:07:00','06/23/2015 03:07:00 am','America/Los_Angeles','2015-06-19 14:01:19',15,'https://s3-us-west-2.amazonaws.com/galen-shennanigans-2/standard-waiver.txt','http://mybibs.co','90045');
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_alert`
--

DROP TABLE IF EXISTS `event_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_alert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_50cumy843vuq97ve9a76q4rcv` (`event`),
  CONSTRAINT `FK_50cumy843vuq97ve9a76q4rcv` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_alert`
--

LOCK TABLES `event_alert` WRITE;
/*!40000 ALTER TABLE `event_alert` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_cart_item`
--

DROP TABLE IF EXISTS `event_cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `max_age` int(11) NOT NULL,
  `min_age` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` bigint(20) NOT NULL,
  `purchased` int(11) NOT NULL,
  `time_end` datetime DEFAULT NULL,
  `time_end_local` varchar(255) DEFAULT NULL,
  `time_limit` bit(1) NOT NULL,
  `time_start` datetime DEFAULT NULL,
  `time_start_local` varchar(255) DEFAULT NULL,
  `tshirt_colors` varchar(255) DEFAULT NULL,
  `tshirt_image_urls` varchar(255) DEFAULT NULL,
  `tshirt_sizes` varchar(255) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  `event_type` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7v8l6i4mpgnyciahk6ijbag7c` (`event`),
  KEY `FK_8a1kheja8l24fix3bgdrctok4` (`event_type`),
  CONSTRAINT `FK_7v8l6i4mpgnyciahk6ijbag7c` FOREIGN KEY (`event`) REFERENCES `event` (`id`),
  CONSTRAINT `FK_8a1kheja8l24fix3bgdrctok4` FOREIGN KEY (`event_type`) REFERENCES `event_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_cart_item`
--

LOCK TABLES `event_cart_item` WRITE;
/*!40000 ALTER TABLE `event_cart_item` DISABLE KEYS */;
INSERT INTO `event_cart_item` VALUES (1,100,'',2,120,1,'test test test',100,0,'2015-06-23 20:30:13','06/23/2015 10:30:13 pm','\0','2015-06-19 01:00:00','06/19/2015 03:00:00 am','',NULL,'',0,7,NULL,NULL),(2,100,'',2,120,1,'hhk',1,0,'2015-06-19 08:42:10','06/19/2015 11:42:10 am','\0','2015-06-17 21:00:00','06/18/2015 12:00:00 am','',NULL,'',0,0,4,2),(3,97,'',2,120,1,'5k Run',100,3,'2015-06-22 13:25:45','06/22/2015 01:25:45 pm','\0','2015-06-19 00:00:00','06/19/2015 12:00:00 am','',NULL,'',0,6,5,3),(4,93,'test shirt description',NULL,120,1,'test shirt',100,7,'2015-06-22 13:26:18','06/22/2015 01:26:18 pm','\0','2015-06-19 00:00:00','06/19/2015 12:00:00 am','White,Red',NULL,'S,M,L',1,6,5,NULL),(5,999998825,'test',NULL,120,1,'Red Cross',1,1175,'2015-06-23 13:26:38','06/23/2015 01:26:38 pm','\0','2015-06-19 00:00:00','06/19/2015 12:00:00 am','',NULL,'',2,6,5,NULL);
/*!40000 ALTER TABLE `event_cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_cart_item_coupon`
--

DROP TABLE IF EXISTS `event_cart_item_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_cart_item_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` int(11) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `discount_absolute` bigint(20) DEFAULT NULL,
  `discount_relative` double DEFAULT NULL,
  `used` int(11) NOT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_h6eo9gsppqojmbej2dgj22nnm` (`event`),
  CONSTRAINT `FK_h6eo9gsppqojmbej2dgj22nnm` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_cart_item_coupon`
--

LOCK TABLES `event_cart_item_coupon` WRITE;
/*!40000 ALTER TABLE `event_cart_item_coupon` DISABLE KEYS */;
INSERT INTO `event_cart_item_coupon` VALUES (1,97,'AFAFAFAF',NULL,7,3,5);
/*!40000 ALTER TABLE `event_cart_item_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_cart_item_price_change`
--

DROP TABLE IF EXISTS `event_cart_item_price_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_cart_item_price_change` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) DEFAULT NULL,
  `date_end_local` varchar(255) DEFAULT NULL,
  `date_start_local` varchar(255) DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `high_age_threshold` int(11) NOT NULL,
  `low_age_threshold` int(11) NOT NULL,
  `price` bigint(20) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `team` bit(1) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `event_cart_item` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rwvjoeerutgpyky4sx0usvj7j` (`event_cart_item`),
  CONSTRAINT `FK_rwvjoeerutgpyky4sx0usvj7j` FOREIGN KEY (`event_cart_item`) REFERENCES `event_cart_item` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_cart_item_price_change`
--

LOCK TABLES `event_cart_item_price_change` WRITE;
/*!40000 ALTER TABLE `event_cart_item_price_change` DISABLE KEYS */;
INSERT INTO `event_cart_item_price_change` VALUES (1,'jkjkjkjk','06/17/2015 11:29:59.999 pm','06/17/2015 09:00:00.000 pm','2015-06-17 20:29:59',2,120,1,100,'2015-06-17 18:00:00','\0',2,NULL),(2,'jkjkjkjk','06/19/2015 08:42:10.000 am','06/17/2015 11:30:00.000 pm','2015-06-19 05:42:10',2,120,1,100,'2015-06-17 20:30:00','\0',2,NULL),(3,'jkjkjkjk','06/17/2015 11:29:59.999 pm','06/17/2015 09:00:00.000 pm','2015-06-17 20:29:59',2,120,1,100,'2015-06-17 18:00:00','\0',2,NULL),(4,'jkjkjkjk','06/19/2015 11:42:10.000 am','06/17/2015 11:30:00.000 pm','2015-06-19 08:42:10',2,120,1,100,'2015-06-17 20:30:00','\0',2,NULL),(5,'youth','06/18/2015 03:59:59.999 am','06/18/2015 12:00:00.000 am','2015-06-18 00:59:59',0,120,1,100,'2015-06-17 21:00:00','\0',1,2),(6,'youth','06/19/2015 11:42:10.000 am','06/18/2015 04:00:00.000 am','2015-06-19 08:42:10',0,120,1,120,'2015-06-18 01:00:00','\0',1,2);
/*!40000 ALTER TABLE `event_cart_item_price_change` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_map`
--

DROP TABLE IF EXISTS `event_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_o9jmykve6gr47hl5q0hbgnsku` (`event`),
  CONSTRAINT `FK_o9jmykve6gr47hl5q0hbgnsku` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_map`
--

LOCK TABLES `event_map` WRITE;
/*!40000 ALTER TABLE `event_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_photo`
--

DROP TABLE IF EXISTS `event_photo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_photo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kbcplu1pedklwe49vypfbrbbb` (`event`),
  CONSTRAINT `FK_kbcplu1pedklwe49vypfbrbbb` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_photo`
--

LOCK TABLES `event_photo` WRITE;
/*!40000 ALTER TABLE `event_photo` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_photo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_result`
--

DROP TABLE IF EXISTS `event_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p9sigt975bqasasmejclpskek` (`event`),
  CONSTRAINT `FK_p9sigt975bqasasmejclpskek` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_result`
--

LOCK TABLES `event_result` WRITE;
/*!40000 ALTER TABLE `event_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_type`
--

DROP TABLE IF EXISTS `event_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `auto_map_reg` bit(1) NOT NULL,
  `distance` varchar(255) NOT NULL,
  `high_bib` bigint(20) DEFAULT NULL,
  `low_bib` bigint(20) DEFAULT NULL,
  `meters` bigint(20) DEFAULT NULL,
  `racetype` varchar(255) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `time_start_local` varchar(255) DEFAULT NULL,
  `type_name` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7y8mrlfrs5trc8hfnwt0w03cm` (`event`),
  CONSTRAINT `FK_7y8mrlfrs5trc8hfnwt0w03cm` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_type`
--

LOCK TABLES `event_type` WRITE;
/*!40000 ALTER TABLE `event_type` DISABLE KEYS */;
INSERT INTO `event_type` VALUES (1,'\0','10 mi',NULL,NULL,17600,'Running','2015-06-18 08:04:00','06/18/2015 10:04:00 am','Running - 10 mi',4,NULL),(2,'\0','6 mi',NULL,NULL,10560,'Swimming','2015-06-18 21:00:00','06/19/2015 12:00:00 am','Swimming - 6 mi',0,4),(3,'\0','5 mi',NULL,NULL,8800,'Running','2015-06-23 03:00:00','06/23/2015 03:00:00 am','Running - 5 mi',0,5);
/*!40000 ALTER TABLE `event_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_user_group`
--

DROP TABLE IF EXISTS `event_user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_user_group` (
  `version` int(11) DEFAULT NULL,
  `event_id` bigint(20) NOT NULL DEFAULT '0',
  `user_group_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`event_id`,`user_group_id`),
  KEY `FK_ps6i99p6opda40kmulxguqmfu` (`user_group_id`),
  CONSTRAINT `FK_dem6cqdyj6hgwpxtsj5brw7mq` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `FK_ps6i99p6opda40kmulxguqmfu` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_user_group`
--

LOCK TABLES `event_user_group` WRITE;
/*!40000 ALTER TABLE `event_user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `license`
--

DROP TABLE IF EXISTS `license`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `license` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` tinyblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `license`
--

LOCK TABLES `license` WRITE;
/*!40000 ALTER TABLE `license` DISABLE KEYS */;
INSERT INTO `license` VALUES (1,'\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0');
/*!40000 ALTER TABLE `license` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture_hashtag`
--

DROP TABLE IF EXISTS `picture_hashtag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `picture_hashtag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `picture_hashtag` varchar(255) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture_hashtag`
--

LOCK TABLES `picture_hashtag` WRITE;
/*!40000 ALTER TABLE `picture_hashtag` DISABLE KEYS */;
/*!40000 ALTER TABLE `picture_hashtag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture_hashtag_race_image`
--

DROP TABLE IF EXISTS `picture_hashtag_race_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `picture_hashtag_race_image` (
  `image_id` bigint(20) NOT NULL,
  `hashtag_id` bigint(20) NOT NULL,
  PRIMARY KEY (`image_id`,`hashtag_id`),
  KEY `FK_t3o87ina24lhg84wi3hs7ynj` (`hashtag_id`),
  CONSTRAINT `FK_a1j15x6qtab6h09ec7p8c2jsq` FOREIGN KEY (`image_id`) REFERENCES `race_image` (`id`),
  CONSTRAINT `FK_t3o87ina24lhg84wi3hs7ynj` FOREIGN KEY (`hashtag_id`) REFERENCES `picture_hashtag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture_hashtag_race_image`
--

LOCK TABLES `picture_hashtag_race_image` WRITE;
/*!40000 ALTER TABLE `picture_hashtag_race_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `picture_hashtag_race_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race_image`
--

DROP TABLE IF EXISTS `race_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) NOT NULL,
  `non_public` bit(1) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  `user_profile` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_l7m4e1ieec0y2vy49l3rbfjeb` (`event`),
  KEY `FK_3d2cr8dimn1gmntbepjlxawvd` (`user_profile`),
  CONSTRAINT `FK_3d2cr8dimn1gmntbepjlxawvd` FOREIGN KEY (`user_profile`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK_l7m4e1ieec0y2vy49l3rbfjeb` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race_image`
--

LOCK TABLES `race_image` WRITE;
/*!40000 ALTER TABLE `race_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `race_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race_result`
--

DROP TABLE IF EXISTS `race_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL,
  `bib` bigint(20) NOT NULL,
  `checkedin` bit(1) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `disqualified` bit(1) NOT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `laps` int(11) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `licensed` bit(1) NOT NULL,
  `medal` varchar(255) DEFAULT NULL,
  `middlename` varchar(255) DEFAULT NULL,
  `rankage` varchar(255) DEFAULT NULL,
  `rankclass` varchar(255) DEFAULT NULL,
  `rankgender` varchar(255) DEFAULT NULL,
  `rankoverall` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `team` varchar(255) DEFAULT NULL,
  `timed` bit(1) NOT NULL,
  `timeofficial` bigint(20) NOT NULL,
  `timeofficialdisplay` varchar(255) DEFAULT NULL,
  `timesplit` varchar(255) DEFAULT NULL,
  `timestart` bigint(20) NOT NULL,
  `updated` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  `event_type` bigint(20) DEFAULT NULL,
  `user_profile` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bib_index` (`bib`),
  KEY `name_index` (`firstname`,`lastname`),
  KEY `FK_iyw99oj0s8w82got3dicyavh2` (`event`),
  KEY `FK_atcn7rj4ewecp2vp8hfwis0dj` (`event_type`),
  KEY `FK_85uome0t9jnfehpa3ou6b3jwq` (`user_profile`),
  CONSTRAINT `FK_85uome0t9jnfehpa3ou6b3jwq` FOREIGN KEY (`user_profile`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK_atcn7rj4ewecp2vp8hfwis0dj` FOREIGN KEY (`event_type`) REFERENCES `event_type` (`id`),
  CONSTRAINT `FK_iyw99oj0s8w82got3dicyavh2` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=599 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race_result`
--

LOCK TABLES `race_result` WRITE;
/*!40000 ALTER TABLE `race_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `race_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race_result_custom_fields`
--

DROP TABLE IF EXISTS `race_result_custom_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race_result_custom_fields` (
  `race_result` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `field` varchar(255) NOT NULL,
  PRIMARY KEY (`race_result`,`field`),
  CONSTRAINT `FK_qbwlpk4q6frp36v7ercd7t5oc` FOREIGN KEY (`race_result`) REFERENCES `race_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race_result_custom_fields`
--

LOCK TABLES `race_result_custom_fields` WRITE;
/*!40000 ALTER TABLE `race_result_custom_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `race_result_custom_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race_result_race_image`
--

DROP TABLE IF EXISTS `race_result_race_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race_result_race_image` (
  `image_id` bigint(20) NOT NULL,
  `result_id` bigint(20) NOT NULL,
  PRIMARY KEY (`result_id`,`image_id`),
  KEY `FK_cicdhm89ft2r6tyqcos23ob26` (`image_id`),
  CONSTRAINT `FK_cicdhm89ft2r6tyqcos23ob26` FOREIGN KEY (`image_id`) REFERENCES `race_image` (`id`),
  CONSTRAINT `FK_tp0q0nu47haomp2bwb18esd3m` FOREIGN KEY (`result_id`) REFERENCES `race_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race_result_race_image`
--

LOCK TABLES `race_result_race_image` WRITE;
/*!40000 ALTER TABLE `race_result_race_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `race_result_race_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race_result_splits`
--

DROP TABLE IF EXISTS `race_result_splits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race_result_splits` (
  `race_result` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `time` bigint(20) NOT NULL,
  `timediscrete` bigint(20) NOT NULL,
  `timediscretedisplay` varchar(255) DEFAULT NULL,
  `timedisplay` varchar(255) DEFAULT NULL,
  `position` int(11) NOT NULL,
  PRIMARY KEY (`race_result`,`position`),
  CONSTRAINT `FK_58s6j6i581nuuj9reiflb9weu` FOREIGN KEY (`race_result`) REFERENCES `race_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race_result_splits`
--

LOCK TABLES `race_result_splits` WRITE;
/*!40000 ALTER TABLE `race_result_splits` DISABLE KEYS */;
/*!40000 ALTER TABLE `race_result_splits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `results_file`
--

DROP TABLE IF EXISTS `results_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `automatic_updates` bit(1) NOT NULL,
  `content_type` varchar(255) NOT NULL,
  `created` datetime DEFAULT NULL,
  `dropbox_path` varchar(128) DEFAULT NULL,
  `file_path` varchar(128) NOT NULL,
  `filesize` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sha1checksum` varchar(42) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `event` bigint(20) DEFAULT NULL,
  `import_user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8nbpbw74h9vmiac7gw6qi320a` (`event`),
  KEY `FK_7yuohkemq4li2oh6i8xwr2shu` (`import_user`),
  CONSTRAINT `FK_7yuohkemq4li2oh6i8xwr2shu` FOREIGN KEY (`import_user`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK_8nbpbw74h9vmiac7gw6qi320a` FOREIGN KEY (`event`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `results_file`
--

LOCK TABLES `results_file` WRITE;
/*!40000 ALTER TABLE `results_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `results_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `results_file_mapping`
--

DROP TABLE IF EXISTS `results_file_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results_file_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `map` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `skip_first_row` bit(1) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `results_file` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3e4nds8h87v3uahadfffxkpy7` (`results_file`),
  CONSTRAINT `FK_3e4nds8h87v3uahadfffxkpy7` FOREIGN KEY (`results_file`) REFERENCES `results_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `results_file_mapping`
--

LOCK TABLES `results_file_mapping` WRITE;
/*!40000 ALTER TABLE `results_file_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `results_file_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `results_import`
--

DROP TABLE IF EXISTS `results_import`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results_import` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_rows` varchar(255) DEFAULT NULL,
  `errors` int(11) NOT NULL,
  `rows_processed` int(11) NOT NULL,
  `run_date` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `results_file` bigint(20) DEFAULT NULL,
  `results_file_mapping` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7ijt7c57sg341waskpfiih8o6` (`results_file`),
  KEY `FK_gg8ofub2ib5027jfmb37x1jle` (`results_file_mapping`),
  CONSTRAINT `FK_7ijt7c57sg341waskpfiih8o6` FOREIGN KEY (`results_file`) REFERENCES `results_file` (`id`),
  CONSTRAINT `FK_gg8ofub2ib5027jfmb37x1jle` FOREIGN KEY (`results_file_mapping`) REFERENCES `results_file_mapping` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `results_import`
--

LOCK TABLES `results_import` WRITE;
/*!40000 ALTER TABLE `results_import` DISABLE KEYS */;
/*!40000 ALTER TABLE `results_import` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timer_config`
--

DROP TABLE IF EXISTS `timer_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `timer_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_timeout` int(11) NOT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `laps` bit(1) NOT NULL,
  `min_finish` int(11) NOT NULL,
  `ports` varchar(255) DEFAULT NULL,
  `position` int(11) NOT NULL,
  `read_power` int(11) NOT NULL,
  `read_timeout` int(11) NOT NULL,
  `reader_model` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `write_power` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timer_config`
--

LOCK TABLES `timer_config` WRITE;
/*!40000 ALTER TABLE `timer_config` DISABLE KEYS */;
INSERT INTO `timer_config` VALUES (1,0,NULL,'',60,NULL,0,0,1,NULL,0,'tmr://bibs001.bibsmobile.com',0,0),(2,0,NULL,'',60,NULL,1,0,1,NULL,0,'tmr://bibs002.bibsmobile.com',0,0);
/*!40000 ALTER TABLE `timer_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_authorities`
--

DROP TABLE IF EXISTS `user_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_authorities` (
  `version` int(11) DEFAULT NULL,
  `user_authorities` bigint(20) NOT NULL DEFAULT '0',
  `user_profile` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_authorities`,`user_profile`),
  KEY `FK_13a63w5unsfr6w4l6vva3ne85` (`user_profile`),
  CONSTRAINT `FK_13a63w5unsfr6w4l6vva3ne85` FOREIGN KEY (`user_profile`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK_e3rjs3dhaxcr18ne7botil78k` FOREIGN KEY (`user_authorities`) REFERENCES `user_authority` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_authorities`
--

LOCK TABLES `user_authorities` WRITE;
/*!40000 ALTER TABLE `user_authorities` DISABLE KEYS */;
INSERT INTO `user_authorities` VALUES (0,1,1),(0,2,2),(0,2,3),(0,4,4),(0,4,6),(0,4,10),(0,4,14),(0,4,18);
/*!40000 ALTER TABLE `user_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_authority`
--

DROP TABLE IF EXISTS `user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_authority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authority` varchar(255) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_authority`
--

LOCK TABLES `user_authority` WRITE;
/*!40000 ALTER TABLE `user_authority` DISABLE KEYS */;
INSERT INTO `user_authority` VALUES (1,'ROLE_SYS_ADMIN',0),(2,'ROLE_EVENT_ADMIN',0),(3,'ROLE_USER_ADMIN',0),(4,'ROLE_USER',0);
/*!40000 ALTER TABLE `user_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bib_writes` int(11) NOT NULL,
  `group_type` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group_user_authority`
--

DROP TABLE IF EXISTS `user_group_user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_user_authority` (
  `version` int(11) DEFAULT NULL,
  `user_group_id` bigint(20) NOT NULL DEFAULT '0',
  `user_profile` bigint(20) NOT NULL DEFAULT '0',
  `user_authorities` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_profile`,`user_authorities`,`user_group_id`),
  KEY `FK_1n4s2256286j2gnljlj2ipv5g` (`user_group_id`),
  CONSTRAINT `FK_1n4s2256286j2gnljlj2ipv5g` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_ploq1t1bb1oggcnqyg613a74i` FOREIGN KEY (`user_profile`, `user_authorities`) REFERENCES `user_authorities` (`user_authorities`, `user_profile`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group_user_authority`
--

LOCK TABLES `user_group_user_authority` WRITE;
/*!40000 ALTER TABLE `user_group_user_authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_user_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_non_expired` bit(1) NOT NULL,
  `account_non_locked` bit(1) NOT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `credentials_non_expired` bit(1) NOT NULL,
  `dropbox_access_token` varchar(255) DEFAULT NULL,
  `dropbox_id` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `emergency_contact_name` varchar(255) DEFAULT NULL,
  `emergency_contact_phone` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `facebook_id` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `forgot_password_code` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  `hear_from` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `stripe_customer_id` varchar(255) DEFAULT NULL,
  `twitter_id` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile`
--

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;
INSERT INTO `user_profile` VALUES (1,'','',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,'',NULL,'System',NULL,NULL,NULL,NULL,NULL,'Administrator','admin',NULL,NULL,NULL,NULL,'admin',0,NULL),(2,'','',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,'',NULL,'Event',NULL,NULL,NULL,NULL,NULL,'Administrator','eventadmin',NULL,NULL,NULL,NULL,'eventadmin',0,NULL),(3,'','',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,'',NULL,'User',NULL,NULL,NULL,NULL,NULL,'Administrator','useradmin',NULL,NULL,NULL,NULL,'useradmin',0,NULL),(4,'','',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,'',NULL,'Bibs',NULL,NULL,NULL,NULL,NULL,'User','user',NULL,NULL,NULL,NULL,'user',0,NULL),(5,'\0','\0',NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL),(6,'','','galen galen',NULL,'1991-09-21','galen','',NULL,NULL,'gedanziger@gmail.com','galen','9999999999','',NULL,'Galen',NULL,'Male',NULL,'Bibs app',NULL,'Danziger',NULL,'9999999999','California',NULL,NULL,NULL,0,'99999'),(7,'\0','\0',NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL),(8,'\0','\0','219 6th st',NULL,'1991-09-21','San Francisco','\0',NULL,NULL,'galen@mybibs.co','galen','1231231231','\0',NULL,'galen',NULL,'Male',NULL,'Bibs app',NULL,'galen',NULL,'9999999999','California',NULL,NULL,NULL,0,'94103'),(9,'\0','\0','219 6th st',NULL,'1991-09-21','San Francisco','\0',NULL,NULL,'galen@mybibs.co','galen','1231231231','\0',NULL,'galen',NULL,'Male',NULL,'Bibs app',NULL,'galen',NULL,'9999999999','California',NULL,NULL,NULL,0,'94103'),(10,'','','219 6th st',NULL,'1991-09-21','San Francisco','',NULL,NULL,'galen@mybibs.co','galen','1231231231','',NULL,'galen',NULL,'Male',NULL,'Bibs app',NULL,'galen',NULL,'9999999999','California',NULL,NULL,NULL,0,'94103'),(11,'\0','\0',NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL),(12,'\0','\0','904 Haight Street, San Francisco, CA, United States',NULL,'1991-09-21','San Francisco','\0',NULL,NULL,'galen@mybibs.co','galen','9099099090','\0',NULL,'lowercase',NULL,'Male',NULL,'Bibs app',NULL,'coupon',NULL,'1231231231','California',NULL,NULL,NULL,0,'94117'),(13,'\0','\0','904 Haight Street, San Francisco, CA, United States',NULL,'1991-09-21','San Francisco','\0',NULL,NULL,'galen@mybibs.co','galen','9099099090','\0',NULL,'lowercase',NULL,'Male',NULL,'Bibs app',NULL,'coupon',NULL,'1231231231','California',NULL,NULL,NULL,0,'94117'),(14,'','','904 Haight Street, San Francisco, CA, United States',NULL,'1991-09-21','San Francisco','',NULL,NULL,'galen@mybibs.co','galen','9099099090','',NULL,'lowercase',NULL,'Male',NULL,'Bibs app',NULL,'coupon',NULL,'1231231231','California',NULL,NULL,NULL,0,'94117'),(15,'\0','\0',NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL),(16,'\0','\0','904 Haight Avenue, Alameda, CA, United States',NULL,'1991-09-21','Alameda','\0',NULL,NULL,'galen@mybibs.co','Galen','9999999999','\0',NULL,'Galen',NULL,'Male',NULL,'Bibs app',NULL,'Danziger',NULL,'9099099090','California',NULL,NULL,NULL,0,'94501'),(17,'\0','\0','904 Haight Avenue, Alameda, CA, United States',NULL,'1991-09-21','Alameda','\0',NULL,NULL,'galen@mybibs.co','Galen','9999999999','\0',NULL,'Galen',NULL,'Male',NULL,'Bibs app',NULL,'Danziger',NULL,'9099099090','California',NULL,NULL,NULL,0,'94501'),(18,'','','904 Haight Avenue, Alameda, CA, United States',NULL,'1991-09-21','Alameda','',NULL,NULL,'galen@mybibs.co','Galen','9999999999','',NULL,'Galen',NULL,'Male',NULL,'Bibs app',NULL,'Danziger',NULL,'9099099090','California',NULL,NULL,NULL,0,'94501');
/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-19 14:52:13
