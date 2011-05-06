<?php
	require_once('config.php');
	
	if($_REQUEST && $_REQUEST['stacktrace'] && $_REQUEST['package_version'] && $_REQUEST['package_name']) {
		$dbh = new PDO(DB_CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);
		$st = $dbh->prepare("INSERT INTO trace (created, package, version, android_version, phone_model, trace) VALUES (?, ?, ?, ?, ?, ?)");
		$st->execute(array(
			date('Y-m-d H:i:s'),
			$_REQUEST['package_name'],
			$_REQUEST['package_version'],
			$_REQUEST['android_version'],
			$_REQUEST['phone_model'],
			$_REQUEST['stacktrace']
		));
		$dbh = null;
		exit;
	}
?>

This script is used to collect field test crash stacktraces. No personal information is transmitted, collected or stored.
<?if(SUPPORT_EMAIL):?>
<br/>For more information, please contact <a href="mailto:<?=SUPPORT_EMAIL?>"><?=SUPPORT_EMAIL?></a>
<?endif?>

<h2>Post the following data:</h2>
<ul>
	<li>package_name</li>
	<li>package_version</li>
	<li>android_version</li>
	<li>phone_model</li>
	<li>stacktrace</li>
</ul>

<dl>
	<dt>to this URL:</dt>
	<dd><?= $_SERVER['HTTP_PORT']==443?'https':'http' ?>://<?= $_SERVER['HTTP_HOST'] ?><?= $_SERVER['REQUEST_URI'] ?></dd>
</dl>
