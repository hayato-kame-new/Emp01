<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.EmployeeDAO, java.util.*, model.EmployeeBean" %>

<%
// 従業員一覧表示用に、従業員リストの取得
EmployeeDAO empDAO = new EmployeeDAO();
List<EmployeeBean> empList = new ArrayList<EmployeeBean>(); // new でまずメモリ上の確保をする
empList = empDAO.findAll(); // 戻り値は、コレクション ArrayList<EmployeeBean>型オブジェクト


%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員データベース管理ページ</title>
<style>
th {
  background-color: #0099ff;
}
</style>
</head>
<body>
<p>社員一覧:</p>

<table border="1">
<tr>
<th>社員ID</th><th>名前</th><th colspan="2"></th>
</tr>
<% for(EmployeeBean empBean : empList) { %>
<tr>
<td><%= empBean.getEmployeeId() %></td>
<td><%= empBean.getName() %></td>
<td></td>
<td></td>
</tr>
<% } %>
</table>


</body>
</html>