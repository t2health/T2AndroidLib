<?php
require_once('config.php');

//header("Content-type: application/json; charset=utf-8");

$dbh = new PDO(DB_CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);

if($_REQUEST['method'] == 'delete' && $_REQUEST['id']) {
  $st = $dbh->prepare("UPDATE trace SET deleted=1 WHERE id=?");
  $st->execute(array($_REQUEST['id']));
  
  echo json_encode(array(
    'success' => TRUE,
  ));
  exit;
}

$dbh->close();
echo json_encode(NULL);
exit;
?>
