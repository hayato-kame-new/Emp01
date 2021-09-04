<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean"%>
<%
// リクエスト情報とか、リクエストスコープからのスコープ変数の値を取得するための、文字化け対策
request.setCharacterEncoding("UTF-8");
// EmployeeServletから、フォワードで送られて来た。リクエストスコープから取得する getAttributeメソッドを使う
String action = (String) request.getAttribute("action");
EmployeeBean empBean = (EmployeeBean) request.getAttribute("empBean");

String label = action.equals("add") ? "新規作成" : "編集";
// 新規登録の時には、empBeanオブジェクトの各フィールドには、各データ型の規定値が デフォルトとして入っているので、String型などの参照型の規定値はnull だから
// 参照型のフィールドの場合 null だったら、空文字を、フォームの初期値にします。 value属性の値が、フォームの初期値です。
String employeeId = empBean.getEmployeeId() == null ? "" : empBean.getEmployeeId();
String name = empBean.getName() == null ? "" : empBean.getName();
// 新規登録の時、empBeanオブジェクトの 基本データ型(プリミティブ型)のフィールドの規定値は、int型なら 0 です それはそのまま使う
int gender = empBean.getGender(); // 新規では、0 が規定値として入っている 編集時は、男性なら 1  女性なら 2 が取得できる
String zipNumber = empBean.getZipNumber() == null ? "" : empBean.getZipNumber();

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員データ<%=label%></title>
</head>
<body>
  <h2>
    社員データを<%=label%>します
  </h2>

  <!-- フォーム送信先は、チェック用サーブレットです。
 まず、photosテーブルについて操作をできるようにする
 CheckServletでは、@MultipartConfigアノテーションを クラス宣言の上に付けてください -->

  <form action="CheckServlet" enctype="multipart/form-data" method="post">
    <input type="hidden" name="action" value="<%=action%>">

    <p>
      社員ID:<input type="text" name="employeeId" value="<%=employeeId%>" />
    </p>
    <p>
      名前:<input type="text" name="name" value="<%=name%>" />
    </p>
    <p>
      年齢:<input type="text" name="age" value="<%=empBean.getAge()%>" />
    </p>
    <p>
      性別:
      <%
    switch (gender) {
    case 0: //新規の時は 0 がint型の規定値なので入っています。
    %>
      <input type="radio" name="gender" value="1" />男性 <input type="radio"
        name="gender" value="2" />女性
      <%
      break;
      case 1: // 編集時は、男性なら int型の 1 が入っています
      %>
      <input type="radio" name="gender" value="1" checked="checked" />男性 <input
        type="radio" name="gender" value="2" />女性
      <%
      break;
      case 2: // 編集時は、女性なら int型の 2 が入っています
      %>
      <input type="radio" name="gender" value="1" />男性 <input type="radio"
        name="gender" value="2" checked="checked" />女性
      <%
      break;
      }
      %>
    </p>

    <!-- 写真表示 -->
    <p>
      <!-- 写真の表示は、PhotoDisplayServletがコントローラーです クエリー文字列で、キーphotoId 値が empBean.getPhotoId() で送ってるので、 HTTPメソッドは、GETメソッド -->
      写真:<img src="PhotoDisplayServlet?photoId=<%=empBean.getPhotoId()%>"
        alt="写真" title="社員の写真" width="300" height="250" />
    </p>
    <!-- 写真表示ここまで -->
    <!-- 写真アップロード formタグには enctype="multipart/form-data" が必要です。 また、送信先のサーブレットには、クラス宣言のところに @MultipartConfigアノテーション が必要です -->
    <input type="file" name="image" accept=".jpeg, .jpg, .png" /> <input
      type="hidden" name="photoId" value="" />
    <!-- 写真アップロードここまで -->

    <p>
      郵便番号:<input type="text" name="zipNumber" value="<%=zipNumber %>" />
    </p>





    <input type="submit" value="送信" />
    <!-- CheckServletへ行く -->
  </form>

</body>
</html>