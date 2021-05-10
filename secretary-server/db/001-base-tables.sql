CREATE TABLE `validation_schema` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) DEFAULT NULL,
    `active` bit(1) NOT NULL DEFAULT 0,
    `name` varchar(64) NOT NULL,
    `description` varchar(255) NOT NULL,
    `data` blob DEFAULT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_schema_id` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `file_schema` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `workflow` varchar(64) NOT NULL,
    `validation_schema` varchar(2048) NOT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_file_schema_workflow` (`workflow`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `file_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) NOT NULL,
    `name` varchar(64) NOT NULL,
    `count` bigint(20) NOT NULL DEFAULT 0,
    `hashsum` varchar(128) NOT NULL,
    `state` varchar(32) NOT NULL,
    `user` varchar(64) NOT NULL,
    `workflow` varchar(64) NOT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_file_uuid` (`uuid`),
    UNIQUE KEY `uniq_file_hashsum` (`hashsum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `workflow` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    `valid` bit(1) NOT NULL DEFAULT 0,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_workflow_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `transient_raw_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `partition_id` int(11) NOT NULL DEFAULT 1,
    `file_id` varchar(64) NOT NULL,
    `file_index` bigint(20) DEFAULT 1,
    `lookup_key` varchar(64) NOT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`,`partition_id`),
    UNIQUE KEY `uniq_lookup_key_partition_id` (`lookup_key`, `partition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY RANGE (`partition_id`)
(PARTITION `p0` VALUES LESS THAN (1) ENGINE = InnoDB,
 PARTITION `p1` VALUES LESS THAN (2) ENGINE = InnoDB,
 PARTITION `p2` VALUES LESS THAN (3) ENGINE = InnoDB,
 PARTITION `p3` VALUES LESS THAN (4) ENGINE = InnoDB,
 PARTITION `p4` VALUES LESS THAN (5) ENGINE = InnoDB,
 PARTITION `p5` VALUES LESS THAN (6) ENGINE = InnoDB,
 PARTITION `p6` VALUES LESS THAN (7) ENGINE = InnoDB,
 PARTITION `p7` VALUES LESS THAN (8) ENGINE = InnoDB,
 PARTITION `p8` VALUES LESS THAN (9) ENGINE = InnoDB,
 PARTITION `p9` VALUES LESS THAN (10) ENGINE = InnoDB,
 PARTITION `p10` VALUES LESS THAN (11) ENGINE = InnoDB,
 PARTITION `p11` VALUES LESS THAN (12) ENGINE = InnoDB,
 PARTITION `p12` VALUES LESS THAN (13) ENGINE = InnoDB,
 PARTITION `p13` VALUES LESS THAN (14) ENGINE = InnoDB,
 PARTITION `p14` VALUES LESS THAN (15) ENGINE = InnoDB,
 PARTITION `p15` VALUES LESS THAN MAXVALUE ENGINE = InnoDB);