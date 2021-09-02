<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
request.setCharacterEncoding("UTF-8");
String label = request.getParameter("action").equals("depAdd") ? "新規作成" : "編集";
String value = request.getParameter("department") != null ? request.getParameter("department") : "";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>部署データを<%=label %>します</title>
</head>
<body>
<h2>部署データを<%=label %>します</h2>
<form method="post" action="DepartmentServlet">
  部署名:<input type="text" name="department" value="<%=value %>"/>
  <input type="hidden" name="action" value="<%=request.getParameter("action") %>" />
  <input type="submit" value="送信" />
</form>
</body>
</html>