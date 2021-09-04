<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %>
<%
// リクエスト情報とか、リクエストスコープからのスコープ変数の値を取得するための、文字化け対策
request.setCharacterEncoding("UTF-8");
// EmployeeServletから、フォワードで送られて来た。リクエストスコープから取得する getAttributeメソッドを使う
String action = (String)request.getAttribute("action");
EmployeeBean empBean = (EmployeeBean)request.getAttribute("empBean");

String label = action.equals("add") ? "新規作成" : "編集";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員<%=label %></title>
</head>
<body>
<h2>社員データを<%=label %>します</h2>

<!-- フォーム送信先は、チェック用サーブレットです。
 まず、photosテーブルについて操作をできるようにする
 CheckServletでは、@MultipartConfigアノテーションを クラス宣言の上に付けてください -->
<form action="CheckServlet" enctype="multipart/form-data" method="post">
  <input type="hidden" name="action" value="<%=action %>">
  <p>
  <!-- 写真の表示は、PhotoDisplayServletがコントローラーです クエリー文字列で、キーphotoId 値が empBean.getPhotoId() で送ってるので、 HTTPメソッドは、GETメソッド -->
  写真:<img src="PhotoDisplayServlet?photoId=<%=empBean.getPhotoId() %>" alt="写真" title="社員の写真" width="300" height="250" />
  </p>
  <input type="file" name="image" accept=".jpeg, .jpg, .png" />
  <input type="hidden" name="photoId" value="" />
  <input type="submit" value="送信" />
  <!-- CheckServletへ行く -->
</form>

</body>
</html>