package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DepartmentDAO;
import model.DepartmentBean;

/**
 * Servlet implementation class DepartmentServlet
 */
@WebServlet("/DepartmentServlet")
public class DepartmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DepartmentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String department = request.getParameter("department");
        String action = request.getParameter("action");

        // デフォルトのフォワード先、結果ページへのパス
        String path = "/WEB-INF/jsp/result.jsp";
        // 結果ページへメッセージを出す
        String msg = "データベースへの登録に成功しました。";
        String title = "成功";

        DepartmentBean depBean = new DepartmentBean();
        DepartmentDAO deDAO = new DepartmentDAO();
        // departmentIdカラム 主キーが、文字列なので、departmentIdを生成するメソッドを呼び出す
        String generatedId = deDAO.generateId(); // 生成した文字列の主キー departmentId を取得

        depBean.setDepartmentId(generatedId); // 主キーをセット
        depBean.setDepartment(department); // フォームから送られた部署名をセット
        // 新規登録します。新規登録のメソッドを呼び出します 成功したら、true 失敗したら falseを返します。
        boolean result = deDAO.depAdd(depBean);
        if (!result) { // trueじゃなかったら、失敗だから、エラーメッセージを元の画面に返します
            msg = "データベースへの更新に失敗しました";
            title = "失敗";
        }

         // リクエストスコープに保存する リクエストスコープは、フォワードはできる（リダイレクトはできない）
        request.setAttribute("msg", msg);
        request.setAttribute("title", title);
        request.setAttribute("action", action);
        // フォワード
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);




    }

}
