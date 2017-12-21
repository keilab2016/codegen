<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="util.GenerateRandom" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CakePHP Generator</title>
    <link rel = "stylesheet" type = "text/css" href = "css/elements.css"/>
    <link rel = "stylesheet" type = "text/css" href = "css/index.css"/>
	<script type = "text/javascript" src = "js/index.js"/></script>
</head>

<body>
	<div id = "head">
		<h1 id="title"><a href="index.jsp">CakePHP Generator</a></h1>
		<p class="contents">
		<a href="manual.html" target="_blank" class="contents2">CakePHP Generatorのドキュメント</a>
		<a href="c4sa.html" target="_blank" class="contents2">C4SAのマニュアル</a>
		</p>
	</div>
	<form name="form1" method="POST" enctype="multipart/form-data"
		action="GenerateServlet" onclick="document.charset='UTF-8';" onSubmit="return check()">
		<div id = "twoSelect">
		<h4>画面遷移図とER図(クラス図)の.astaファイルが2つに分かれているときに選択</h4>
			<label for="stDiagram">画面遷移図を選択してください：</label>
			<input type="file" id="stDiagram" name="stDiagram" size="20"><br>
			<label for="erDiagram">ER図(クラス図)を選択してください：</label>
			<input type="file" id="dataDiagram" name="dataDiagram" size="20">
		</div>
		<div id = "oneSelect">
		<h4>画面遷移図とER図(クラス図)の.astaファイルが1つにまとまっているときに選択</h4>
			<label for="diagram">.astaファイルを選択してください：</label>
			<input type="file" id="diagram" name="diagram" size="20"><br>
		</div>
		<br>
		<label for="geneWay">生成方式を選択
		<sub><span style="cursor: pointer" onclick="popup('question.html')"><img border="0" src="img/question.png" width="15px" height="15px"></span></sub>
		：</label>
		<input type="radio" id="geneWay1" name="geneWay" value="simple">MVCコードのみ
		<input type="radio" id="geneWay2" name="geneWay" value="full" checked>CakePHPプロジェクト全部入り
		<input type="radio" id="geneWay3" name="geneWay" value="c4sa">C4SA用プロジェクト全部入り
		<br><br>
		<label for="secuSalt">ランダムな文字列を入力してください(半角英数字、20〜50文字程)：</label>
		<input type="text" id="secuSalt" name="secuSalt" value="<%= GenerateRandom.randomText("abcdefghijklmnopqrstuvwxyz", 32) %>"/>
		<br>
		<label for="ciphSeed">ランダムな数字を入力してください(半角数字、20〜50文字程)：</label>
		<input type="text" id="ciphSeed" name="ciphSeed" value="<%= GenerateRandom.randomText("0123456789", 32) %>"/>
		<br>
		<label for="apiKey">API_KEYを入力してください(地図機能を使う場合)：</label>
		<input type="text" id="apiKey" name="apiKey" />
		<br><br>
		<input type="submit" value="自動生成" />
	</form>
	<div id="foot">
		<p>
		<%=System.getProperty("java.version") %>
		<%="-" %>
		<%=System.getProperty("os.name") %>
		</p>
	</div>
</body>
</html>