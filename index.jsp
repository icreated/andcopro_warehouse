<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr" lang="fr">
<head>
<title>Warehouse</title>

<style type="text/css">
<!--
body { margin: 0; font-family: Arial; background: url(images/furley.png); }
#main { width: 559px; height: 206px; margin: 100px auto; -moz-border-radius: 5px; -webkit-border-radius: 5px; border-radius: 5px; }
#main div, form { float: left; display: block; border: 1px solid #ddd; padding: 30px 0; text-decoration: none; font-size: 15px; color: #666; background: #fcfcfc; }
#main div img { display: block; margin: auto; }
h1 { width: 559px; text-align: center; color: #555; font-size: 30px; font-weight: normal; margin-bottom: 24px; text-shadow: 0px 1px 1px #fff; }
a span { display: block; width: 130px; height: 34px; text-align: center; padding: 20px 30px; }
#btn1 { width: 260px; -moz-border-radius: 5px 0 0 5px; -webkit-border-radius: 5px 0 0 5px; border-radius: 5px 0 0 5px; }
form { -moz-border-radius: 0 5px 5px 0; -webkit-border-radius: 0 5px 5px 0; border-radius: 0 5px 5px 0; height: 120px; margin: 0 0 0 -1px; border-left: 1px solid #e5e5e5; padding: 35px 40px 65px 40px; }
form input { float: left; clear: both; width: 200px; border: 1px solid #ddd; padding: 7px; font-size: 16px; color: #333; margin-bottom: 4px; -moz-border-radius: 2px; -webkit-border-radius: 2px; border-radius: 2px; outline: none; }
form input:hover, form input:focus { border: 1px solid #ccc; }
form input#submit { width: 90px; color: #0368c2; background: #fff; border: 1px solid #0368c2; margin-bottom: 0; }
form input#submit:hover { color: #fff; background: #0368c2; border: 1px solid #0368c2; }
form input#submit:active { -webkit-box-shadow: inset 1px 1px 1px 0px rgba(0, 0, 0, 0.1); box-shadow: inset 1px 1px 4px 0px rgba(0, 0, 0, 0.35); }
.message { float: left; color: red; margin: 0 0 8px 0; }
-->
</style>
</head>

<body>

<div id="main">
    <h1>Warehouse</h1>
    <div id="btn1">
        <img src="images/warehouse.png" alt="" />
    </div>
    <form action="login" method="post">
		<c:id test="${!empty message}">
		    <c:out value="${message}"/>
		</c:if>
        <input type="text" name="username" placeholder="id" />
        <input type="password" name="password" placeholder="password" />
        <input type="submit" id="submit" value="Log In" />
    </form>
</div>


</body>
</html>