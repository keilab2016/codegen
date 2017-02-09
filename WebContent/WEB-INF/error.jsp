<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.Generation"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CakePHP Generator</title>
<link rel = "stylesheet" type = "text/css" href = "css/elements.css"/>
<%
Generation generation = (Generation) request.getAttribute("generation");
%>
</head>
<body>
	<div id = "head">
		<h1 id="title"><a href="index.jsp">CakePHP Generator</a></h1>
		<p class="contents">
		<a href="manual.html" target="_blank" class="contents2">CakePHP Generatorのドキュメント</a>
		<a href="c4sa.html" target="_blank" class="contents2">C4SAのマニュアル</a>
		</p>
	</div>
	<h2>エラーが発生しました。</h2>
	<h3>エラーメッセージ</h3>
	<p><%=generation.getErrorMessage() %></p>
</body>
</html>