CREATE TABLE `tvds_part`
(
    `imageID`    varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
    `imageUrl`   varchar(500) COLLATE utf8mb4_general_ci          DEFAULT NULL,
    `status`     tinyint                                 NOT NULL DEFAULT '1',
    `inspection` int                                              DEFAULT NULL,
    `time`       date                                    NOT NULL,
    `seat`       int                                              DEFAULT NULL,
    `carriageID` int                                              DEFAULT NULL,
    `createTime` datetime                                         DEFAULT NULL,
    `updateTime` datetime                                         DEFAULT NULL,
    `isDeleted`  tinyint                                 NOT NULL DEFAULT '0',
    PRIMARY KEY (`imageID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci

CREATE TABLE `tvds_carriage`
(
    `imageID`    varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
    `originUrl`  varchar(500) COLLATE utf8mb4_general_ci          DEFAULT NULL,
    `alignedUrl` varchar(500) COLLATE utf8mb4_general_ci          DEFAULT NULL,
    `status`     tinyint                                 NOT NULL DEFAULT '1',
    `inspection` int                                              DEFAULT NULL,
    `time`       date                                    NOT NULL,
    `seat`       int                                              DEFAULT NULL,
    `carriageNo` int                                              DEFAULT NULL,
    `model`      varchar(100) COLLATE utf8mb4_general_ci          DEFAULT NULL,
    `carriageID` int                                              DEFAULT NULL,
    `createTime` datetime                                         DEFAULT NULL,
    `updateTime` datetime                                         DEFAULT NULL,
    `isDeleted`  int                                     NOT NULL DEFAULT '0',
    PRIMARY KEY (`imageID`),
    UNIQUE KEY `imageID` (`imageID`),
    UNIQUE KEY `carriageID` (`carriageID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci

