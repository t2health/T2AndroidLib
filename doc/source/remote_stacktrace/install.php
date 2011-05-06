<?php
	require_once('config.php');

	$dbh = new PDO(DB_CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);
	$dbh->query("
    CREATE TABLE `trace` (
      `id` int(10) NOT NULL AUTO_INCREMENT,
      `created` datetime NOT NULL,
      `package` varchar(255) NOT NULL,
      `version` varchar(25) NOT NULL,
      `trace` text NOT NULL,
      `android_version` varchar(100) NOT NULL,
      `phone_model` varchar(100) NOT NULL,
      `deleted` int(1) NOT NULL,
      PRIMARY KEY (`id`)
    );
	");
?>
