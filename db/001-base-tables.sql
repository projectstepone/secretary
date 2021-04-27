CREATE TABLE `validation_schema` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) DEFAULT NULL,
    `active` bit(1) NOT NULL DEFAULT 0,
    `name` varchar(64) NOT NULL,
    `version` int(10) NOT NULL DEFAULT 1,
    `data` blob DEFAULT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_schema_id` (`uuid`),
    UNIQUE KEY `uniq_schema` (`name`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `file_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) NOT NULL,
    `name` varchar(64) NOT NULL,
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