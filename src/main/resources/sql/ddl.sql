CREATE TABLE `spike_orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) DEFAULT NULL,
  `money` double(10,2) DEFAULT NULL,
  `complete_time` timestamp NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=130010 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `spike_sale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `price` decimal(10,0) DEFAULT NULL,
  `fee` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sales` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nick` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `img` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `detail_url` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
