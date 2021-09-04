package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.PhotoDAO;
import model.EmployeeLogic;

/**
 * Servlet implementation class CheckServlet
 * @MultipartConfigアノテーション クラス宣言の上に付けてください
 */
@WebServlet("/CheckServlet")
@MultipartConfig
public class CheckServlet extends HttpServlet {
    // @MultipartConfigアノテーションを クラス宣言の上に付けてください
    private static final long serialVersionUID = 1L;

    // ファイルのアップロード時のパターンチェック
    private final Pattern PATTERN_IMAGE = Pattern.compile("^image\\/(jpeg|png)$");

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // リクエストパラメータの文字コードをUTF-8に設定
        request.setCharacterEncoding("UTF-8");
        List<String> errMsgList = new ArrayList<String>(); // エラーなければ、空のリスト  [] と表示されます
        /* まず、写真から ここから */
        // hiddenフィールドから 送られて来た
        String action = request.getParameter("action"); // add もしくは edit
        // 画像のアップロード
        Part part = request.getPart("image");
        String mime = part.getContentType(); // image/jpeg など入ってくる
        // このファイルのサイズを返します   戻り値・・このパートのバイト長を示すlong
        long partSize = part.getSize(); // 2056935 とか
        // 新規登録の時には、必須だから 2350288  などと入ってくる　　編集の時選択しないと 0 が入る これで判断する
        // 社員データの新規登録の時に、写真も、必須としている 編集時はスルー
        if (action.equals("add")) {
            if (part == null || partSize == 0) {
                errMsgList.add("画像が選択されていません");
            }
        }
        // 画像処理 partのサイズが  0より大きければ、対応する   入らないこともあるので  編集の時選択しないと 0 が入る これで判断する
        // 編集の時は、ファイルサイズ0の時はスルーする  未選択を特定する
        if (partSize > 0 && !PATTERN_IMAGE.matcher(mime).matches()) {
            errMsgList.add("画像の形式はJPEGおよびPNGにしてください");
        }
        /* ここまで、写真のアップロードについて  */

        // フォワード先のパス のローカル変数
        String path = "";
        // 結果ページへ送るためのデフォルト値
        String title = "成功";
        String msg = "データベースへ登録に成功しました。";

        // エラーリストのサイズが 0 以外の時(つまり、エラー発生)また、再入力してもらう
        if (errMsgList.size() != 0) {
            // フォワード先のパス リクエストスコープにセットして、フォーワードする(リクエストスコープはリダイレクトはできない)
            path = "EmployeeServlet"; // また、action が add の場合は、新インスタンス生成して送ることになる

        } else {
            // エラーリストのサイズが 0の時は、次は、データベースの操作へ行く
            // データベース成功後、失敗後、結果ページへフォワードするのでフォワード先のパスは
            path = "/WEB-INF/jsp/result.jsp";
            // この後、デーベース操作 まず、photosテーブルから
            // photosテーブルにデータを登録するためには、MIMEタイプと、byte型配列 byte[] のファイルデータが必要です
            // InputStream is = part.getInputStream(); // 画像ストリームの取得
            InputStream is = null;
            try {
                is = part.getInputStream(); // 画像ストリームの取得
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  取得した画像ストリームを、byte配列に格納して返すメソッドを使う readAll(引数)クラスメソッド
            byte[] photoData = EmployeeLogic.readAll(is);  // [-1, -40, -1, -32, ずっと続く 配列が入ってる

            switch (action) {
            case "add": // 設定(新規)ボタンをクリック  新規では、写真は必ず登録される、として
                // アップロードした写真データを  photoテーブル  に新規登録 PhotoDAO の photoDataAddインスタンスメソッド を使う
                // 上で String mime = part.getContentType();でMIMEタイプを取得してるので 引数にMIMEタイプを指定
                // photoDataAddメソッドは、返り値として、主キー int型の photoId の 値を返す
            	PhotoDAO phoDAO = new PhotoDAO();
            	// photosテーブルに新規データを登録して、そのphotoIdが返る photoDataAddメソッドを使う
            	// 返り値が 0 だったら、失敗してる
            	int photoId = phoDAO.photoDataAdd(photoData, mime);
            	if (photoId == 0) {
            		msg = "photosテーブルへの登録に失敗しました。";
            		title = "失敗";
            	} else {
            		// photosテーブルの登録には成功してます。
            		// 次に、employeesテーブルの処理をする。
            		
            	}
            	break; // case句を抜けるのに必要
            }

        }

        // リクエストスコープに保存
        request.setAttribute("action", action);
        request.setAttribute("errMsgList", errMsgList); // エラー無い時には、フォワード先では、リストオブジェクトの要素数が 0 です。nullじゃない。
        // エラーリストの中身の要素数が 0以外の時、（つまり、エラーはある）再度入力してもらう
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);

    }

}
