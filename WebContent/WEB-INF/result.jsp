<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.Generation"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CakePHP Generator</title>
<link rel = "stylesheet" type = "text/css" href = "css/elements.css"/>
<link rel = "stylesheet" type = "text/css" href = "css/result.css"/>
<% 
Generation generation = (Generation) request.getAttribute("generation");
String zipName = generation.getID() + ".zip";
String sqlName = generation.getID() + ".sql"; 
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
	<p id = completion>自動生成が完了しました。</p>
	<div id = "zip">
		プロジェクトファイルをダウンロードする
		<form name="form1" method="post" action="generated/<%=zipName %>">
			<input type="submit" value="ダウンロード" /><font size="1"><sub><%=zipName %></sub></font>
		</form>
	</div>
	<div id = "sql">
		sqlファイルをダウンロードする
		<form name="form2" method="post" action="generated/<%=sqlName %>">
			<input type="submit" value="ダウンロード" /><font size="1"><sub><%=sqlName %></sub></font>
		</form>
	</div>
	<br>
	<h3>Warning リスト</h3>
	<% if(generation.getWarnings().size() == 0) out.println("<p>なし</p>"); %>
	<% for(String warning : generation.getWarnings()) {%>
	<p><%=warning %></p>
	<%} %>
</body>
</html>