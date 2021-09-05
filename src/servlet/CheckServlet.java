package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import dao.PhotoDAO;
import model.EmployeeBean;
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
    // パターンチェック
    private final Pattern PATTERN_AGE = Pattern.compile("^[0-9]*$");
    private final Pattern PATTERN_ZIPNUMBER = Pattern.compile("^[0-9]{3}-[0-9]{4}$");
    private final Pattern PATTERN_DATE = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");

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

        // hiddenフィールドから 送られて来た リクエストパラメータから取得
        String action = request.getParameter("action"); // add もしくは edit
        // hiddenフィールドから 送られてきた photoId は、新規では 文字列の "0" が入ってる
        String strPhotoId = request.getParameter("photoId");
        // int型に変換する これは、編集の時だけ使う
        int photoId = Integer.parseInt(strPhotoId);

        /* まず、写真のアップロードを リクエストパラメータから取得して 入力チェックする*/
        Part part = request.getPart("image");
        String mime = part.getContentType(); // image/jpeg など入ってくる
        // このファイルのサイズを返します   戻り値・・このパートのバイト長を示すlong
        long partSize = part.getSize(); // 2056935 とか
        // 新規登録の時には、必須だから 2350288  などと入ってくる　　編集の時選択しないと 0 が入る これで判断する
        // 入力チェックする
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
        /* ここまで、写真のアップロードについて  リクエストパラメータから取得して、入力チェックまで*/

        /* ここから写真以外を リクエストパラメータから取得して 入力チェックする*/
        String name = request.getParameter("name");
        String strAge = request.getParameter("age"); // 入力チェックした後に、int型に変換する
//        int age = Integer.parseInt(request.getParameter("age"));
        String strGender = request.getParameter("gender"); // 入力チェックした後に、int型に変換する
//        int gender = Integer.parseInt(request.getParameter("gender"));
        String zipNumber = request.getParameter("zipNumber");
        String pref = request.getParameter("pref");
        String address = request.getParameter("address");
        // departmentId は、渡って来ないので、department を取得してから、departmentIdをデータベースから検索してくる
        String department = request.getParameter("department");
        String strHireDate = request.getParameter("hireDate"); // 入力チェックしてから、java.util.Date型に変換する
        // 何も入力しないと、""　空文字が入ってる
        String strRetirementDate = request.getParameter("retirementDate");// 入力チェックしてから、java.util.Date型に変換する

        /*
         入力チェックを行う  null または 空文字かどうかを調べます。 注意あり
         length()メソッドは、"" 空文字かどうかを調べます isEmpty()メソッドでもいいです
         注意点、name == null を先に書いてください、
         なぜなら、name.length() == 0 を先に書くと、nameが nullの場合、にNullPointerExceptionが発生します。
         name == null を先に書くと、nameが nullの場合、str == nullはtrueと評価されるため、式全体がtrueとなり、後者のname.isEmpty()は評価されずNullPointerExceptionは発生しません。
         */


        if (name == null || name.length() == 0) {
            errMsgList.add("名前が入力されていません");
        }

        if (strAge == null || strAge.length() == 0) {
            errMsgList.add("年齢が入力されていません");
        } else if(!PATTERN_AGE.matcher(strAge).matches()){ // 入力チェックOKなら
            errMsgList.add("年齢は半角数字で入力してください");
        }

        if (strGender == null || strGender.length() == 0) {
            errMsgList.add("性別が選択されていません");
        }

        if (pref == null || pref.length() == 0) {
            errMsgList.add("都道府県が選択されていません");
        }

        if (address == null || address.length() == 0) {
            errMsgList.add("住所が入力されていません");
        }

        if (department == null || department.length() == 0) {
            errMsgList.add("所属が選択されていません");
        }

        // 新規登録する時に、入社日は必須です
        if (strHireDate == null || strHireDate.length() == 0){
            errMsgList.add("入社日が入力されていません");
        } else if (!PATTERN_DATE.matcher(strHireDate).matches()) {
            errMsgList.add("入社日は半角数字でハイフン(-)を入れて入力してください　例( 2016-03-20 )");
        }

        // 退社日は、新規登録でも 編集でも、必須ではないので、 null でも、"" 空文字でもOKです。
        // 何か入力されていた時に、チェックをします。パターンにあってるかどうか
        if (strRetirementDate == null || strRetirementDate.length() == 0){
            // 何もしない
        } else if (!PATTERN_DATE.matcher(strRetirementDate).matches()) {
            errMsgList.add("退社日は半角数字でハイフン(-)を入れて入力してください　例( 2016-03-20 )");
        }

        /* ここまで、写真以外について  リクエストパラメータから取得して、入力チェックまで*/




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
                // photosテーブルに新規データを登録して、生成したgeneratedPhotoIdが返る photoDataAddメソッドを使う
                // 返り値が 0 だったら、失敗してる
                int generatedPhotoId = phoDAO.photoDataAdd(photoData, mime);
                if (generatedPhotoId == 0) {
                    msg = "photosテーブルへの登録に失敗しました。"; //結果ページへの出力のため
                    title = "失敗"; // 結果ページへの出力のため
                    break; // case句を抜ける
                } else {
                    // photosテーブルの登録には成功してます。成功したら、employeesテーブルの登録をします。
                    // 入力チェックをしたので、まず、リクエストパラメータから取得したものを、データベースのカラムのデータ型に変換します。
                    int age = Integer.parseInt(request.getParameter("age"));  // int型に変換する
                    int gender = Integer.parseInt(request.getParameter("gender")); // int型に変換する
                    // フォーマット
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    // 入社日は、必須なので nullでもないし、""空文字でもないし、パターンもチェック済み String型からjava.util.Date型へ変換
                    Date hireDate = null; // 初期化
                    try {
                        hireDate = sdf.parse(strHireDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // 退社日 nullもあります。 もし、nullじゃなかったら、String型から java.util.Date型へ変換する
                    Date retirementDate = null;
                    if (strRetirementDate != null && strRetirementDate.length() != 0) {
                        try {
                            retirementDate = sdf.parse(strRetirementDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    // departmentを元に departmentIdをデーベースから検索する
                    DepartmentDAO depDAO = new DepartmentDAO();
                    String departmentId = depDAO.getDepartmentId(department);
                    // employeeId生成する generatedId()メソッドで 社員IDを取得します 失敗したらnullを返す
                    EmployeeDAO empDAO = new EmployeeDAO();
                    String employeeId = empDAO.generatedId();
                    if (employeeId == null) { // employeeId生成 失敗
                        msg = "employeesテーブルへの登録に失敗しました。";
                        title = "失敗";
                        break; // case句抜ける
                    } else { // employeeId生成できたら、employeeBeanインスタンスを生成する
                        // 引数ありのコンストラクタを呼ぶ
                        EmployeeBean empBean = new EmployeeBean(employeeId, name, age, gender, generatedPhotoId, zipNumber, pref, address, departmentId, hireDate, retirementDate);
                        // データベースに登録する
                        boolean result = empDAO.add(empBean);
                        if(!result) { // 失敗の時
                             msg = "employeesテーブルへの登録に失敗しました。";
                             title = "失敗";
                             break; // case句抜ける
                        }
                        // ここに来たら成功してる
                    }
                }
                break; // case句を抜けるのに必要
            }

        }

        // リクエストスコープに保存
        request.setAttribute("action", action);
        request.setAttribute("errMsgList", errMsgList); // エラー無い時には、フォワード先では、リストオブジェクトの要素数が 0 です。nullじゃない。
        request.setAttribute("msg", msg);
        request.setAttribute("title", title);
        // エラーリストの中身の要素数が 0以外の時、（つまり、エラーはある）再度入力してもらう
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);

    }

}
