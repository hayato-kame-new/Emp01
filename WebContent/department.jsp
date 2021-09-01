<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.DepartmentDAO, java.util.*, model.DepartmentBean" %>


<%
// 部署一覧を取得する
DepartmentDAO depDAO = new DepartmentDAO();
List<DepartmentBean> depList = new ArrayList<DepartmentBean>();
depList = depDAO.findAll();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>部署データベース一覧</title>
<style>
th {
  background-color: #0099ff;
}
</style>
</head>
<body>

<p>部署一覧:</p>
  <table border="1">
    <tr>
      <th>ID</th>
      <th>部署名</th>
    </tr>
    <% for(DepartmentBean depBean : depList) {%>
    <tr>
      <td><%=depBean.getDepartmentId() %></td>
      <td><%=depBean.getDepartment() %></td>
      <td></td>
      <td></td>
    </tr>
    <% } %>
  </table>
  <p>
  <a href="#"><button type="button">新規作成</button></a>
  </p>

</body>
</html>