CREATE TABLE `validation_schema` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) DEFAULT NULL,
    `active` bit(1) DEFAULT NULL,
    `name` varchar(64) NOT NULL,
    `version` int(10) NOT NULL DEFAULT 1,
    `data` blob DEFAULT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_schema_id` (`uuid`),
    UNIQUE KEY `uniq_schema` (`name`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
