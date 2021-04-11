CREATE TABLE `rad` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `template_id` varchar(64) DEFAULT NULL,
    `active` bit(1) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `data` blob DEFAULT NULL,
    `created` datetime(3) NOT NULL DEFAULT current_timestamp(3),
    `updated` datetime(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
