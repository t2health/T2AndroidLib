<?php
	require_once('config.php');

	$searchPerformed = false;
	$traces = array();
	if($_REQUEST['package_name'] || $_REQUEST['package_version']) {
		$where = array();
		$where_vals = array();
		
		if($_REQUEST['package_name']) {
			$where[] = "package=?";
			$where_vals[] = $_REQUEST['package_name'];
		}
		if($_REQUEST['package_version']) {
			$where[] = "version=?";
			$where_vals[] = $_REQUEST['package_version'];
		}
		
		$where[] = "deleted!=?";
		$where_vals[] = "1";

		$searchPerformed = true;
		$dbh = new PDO(DB_CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);
		$st = $dbh->prepare("SELECT * FROM trace WHERE ". implode(" AND ", $where) ." ORDER BY created DESC LIMIT 50");
		$st->execute($where_vals);
		
		while($obj = $st->fetchObject()) {
			$traces[] = $obj;
		}
	}
?>

<html>
	<head>
		<title>Remote Exception Logger</title>
		<link rel="stylesheet" href="css/default.css" type="text/css" /> 
		<script type="text/javascript" src="js/jquery.js"></script> 
		<script type="text/javascript" src="js/default.js"></script> 
	</head>
	<body>
		<div id="body">
			<div id="header">
				<h1>Telehealth and Technology</h2>
			</div>
	
			<div id="sidebarRight">
				<form id="filterForm" method="GET">
					<div class="formElement">
						<label for="package_name">Package Name:</label>
						<input type="text" name="package_name" id="package_name" value="<?= $_REQUEST['package_name'] ?>" title="package name: e.g. com.t2.app" />
					</div>
					<div class="formElement">
						<label for="package_version">Package Version:</label>
						<input type="text" name="package_version" id="package_version" value="<?= $_REQUEST['package_version'] ?>" title="package version: e.g. 1.1.2" />
					</div>
					<input type="submit" value="Filter" />
				</form>
			</div>

			<div id="mainContent">
				<h2>Remote Exception Logger</h2>
			<? if($searchPerformed && count($traces)):?>
				<div id="results">
					<? foreach($traces as $trace) {?>
						<div class="result" dbid="<?= $trace->id ?>">
							<div class="header">
								<div class="date"><?= date('F j, Y, H:i:s', strtotime($trace->created)) ?></div>
								<div class="package"><?= $trace->package ?></div>
								<div class="version"><?= $trace->version ?></div>
							</div>

							<div class="trace">
							  <input type="button" class="delete" value="Delete" />
							  <div>Android Version: <?= $trace->android_version ?></div>
							  <div>Phone Model: <?= $trace->phone_model ?></div>
								<pre><?= $trace->trace ?></pre>
							</div>
						</div>
					<? } ?>
				</div>
			<?elseif($searchPerformed):?>
				<p>No stack traces were found.</p>
			<?else:?>
				<p>Use the form on the side to search for stack traces.</p>
			<?endif?>
			</div>

			<div id="footer">
				<a href="report.php">API</a>
			</div>
		</div>
	</body>
</html>
