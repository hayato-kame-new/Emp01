package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.EmployeeBean;

public class EmployeeDAO {

    final String DRIVER_NAME = "org.postgresql.Driver";
    final String JDBC_URL = "jdbc:postgresql://localhost:5432/emp01";
    final String DB_USER = "postgres";
    final String DB_PASS = "postgres";

    /**
     * 従業員のリストを取得する 失敗した時、null返す
     * @return empList
     */
    public List<EmployeeBean> findAll() {
        // 中身は、ArrayListで作ること、ループで取り出したいから 型パラメータ JavaBeansのEmployeeBeanオブジェクト
        List<EmployeeBean> empList = new ArrayList<EmployeeBean>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // PostgreSQLだと、order by employeeId が無いと、データ更新すると、一番後ろになってしまうので、必要
            String sql = "select * from employees order by employeeId";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String employeeId = rs.getString("employeeId");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int gender = rs.getInt("gender");
                int photoId = rs.getInt("photoId");
                String zipNumber = rs.getString("zipNumber");
                String pref = rs.getString("pref");
                String address = rs.getString("address");
                String departmentId = rs.getString("departmentId");
                // 入社日を   sql.Date型から、 util.Date型に変換
                // rs.getDate(引数) は、戻り値が、java.sql.Date型のオブジェクトになってるから
                java.sql.Date sqlHireDate = rs.getDate("hireDate");
                java.util.Date utilHireDate = new java.util.Date(sqlHireDate.getTime());
                // 退社日を   sql.Date型から、 util.Date型に変換
                // 注意、退社日は、nullの可能性がありますので、nullチェックする
                // nullだと、util.Date型に変換に変換する時に、エラー発生するので、
                // nullじゃなければ、変換するようにする
                java.sql.Date sqlRetirementDate = rs.getDate("retirementDate");
                // sqlRetirementDate が nullであれば、util.Date型オブジェクトにnullを入れておく
                java.util.Date utilRetirementDate = null;
                if (sqlRetirementDate != null) {
                    utilRetirementDate = new java.util.Date(sqlRetirementDate.getTime());
                }
                // ループ内で、新しい、EmployeeBeanオブジェクトを生成していく
                EmployeeBean empBean = new EmployeeBean();
                // 結果セットに取り出したデータを  EmployeeBeanインスタンスのフィールドにセットする
                empBean.setEmployeeId(employeeId);
                empBean.setName(name);
                empBean.setAge(age);
                empBean.setGender(gender);
                empBean.setPhotoId(photoId);
                empBean.setZipNumber(zipNumber);
                empBean.setPref(pref);
                empBean.setAddress(address);
                empBean.setDepartmentId(departmentId);
                empBean.setHireDate(utilHireDate);
                empBean.setRetirementDate(utilRetirementDate);
                // リストにEmployeeBeanオブジェクトをセットします。
                empList.add(empBean);
            }
        } catch (SQLException | ClassNotFoundException e) {// データベース接続やSQL実行失敗時の処理 JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return null; // エラーの時は、nullを返すようにする。
        } finally {
            if (rs != null) { //close()する順番は、逆からする
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
        }
        return empList; // 成功したら、リストを返す、１つも、中身のオブジェクトが無いときは、空のリストを返します。
    }

    /**
     * 社員IDを生成する 失敗したら、null返す
     * @return employeeId
     */
    public String generatedId() {
        String employeeId = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // SELECT文を準備
            String sql = "select employeeId from employees order by employeeId desc limit 1";
            // 準備したSQLをデータベースに届けるPrepareStatementインスタンスを取得する
            pstmt = conn.prepareStatement(sql);
            // SQLを実行し、結果はResultSetインスタンスに格納される
            rs = pstmt.executeQuery();
            // 一つ分のデータだけ結果セットに入ってるから while じゃなくて if
            if (rs.next()) {
                String result = rs.getString(1); // rs.getString("employeeId") でもいいです
                employeeId = String.format("EMP%04d", Integer.parseInt(result.substring(3)) + 1);
            } else { // まだ、テーブルに登録されていなかった場合 データが一つもない場合
                employeeId = "EMP0001"; // 一番目のデータ
            }
        } catch (Exception e) { // Exceptionクラスのインスタンスでキャッチする
            e.printStackTrace();
            return null; // 失敗したら、nullを返す
        } finally {
            // PrepareStatementインスタンス、ResultSetインスタンスのクローズ処理
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら、nullを返す
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null;// 失敗したら、nullを返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return null;// 失敗したら、nullを返す
                }
            }
        }
        return employeeId;
    }

    /**
     * 社員を新規登録する
     * @param empBean
     * @return true 成功<br>false 失敗
     */
    public boolean add(EmployeeBean empBean) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // SELECT文を準備 PostgreSQL は、varchar以外には ?::integer  ?::status  ?::bytes  ?::date   などが必要です
            String sql = "insert into employees values (?, ?, ?::integer, ?::integer, ?::integer, ?, ?, ?, ?, ?::date, ?::date)";
            // 準備したSQLをデータベースに届けるPrepareStatementインスタンスを取得する
            pstmt = conn.prepareStatement(sql);
            // プレースホルダーにセットする
            pstmt.setString(1, empBean.getEmployeeId());
            pstmt.setString(2, empBean.getName());
            pstmt.setInt(3, empBean.getAge());
            pstmt.setInt(4, empBean.getGender());
            pstmt.setInt(5, empBean.getPhotoId());
            pstmt.setString(6, empBean.getZipNumber());
            pstmt.setString(7, empBean.getPref());
            pstmt.setString(8, empBean.getAddress());
            pstmt.setString(9, empBean.getDepartmentId());

            // empBean.getHireDate()   empBean.getRetirementDate() で得られる値は、java.util.Date型です。
            java.util.Date utilHire = empBean.getHireDate(); // nullはない
            java.util.Date utilRetire = empBean.getRetirementDate(); // nullありうる
            // 入社日を java.sql.Date型に変換 入社日は、必ず入力してもらうので、 null はない
            java.sql.Date sqlHire = new java.sql.Date(utilHire.getTime());

            // 退社日を java.sql.Date型に変換 ただし、退職日は、nullの可能性がある
            java.sql.Date sqlRetire = null;
            if (utilRetire != null) { // nullだと変換するとエラー発生するので、nullじゃなかった時だけ変換する
                sqlRetire = new java.sql.Date(utilRetire.getTime());
            }
            // セット
            pstmt.setDate(10, sqlHire);
            pstmt.setDate(11, sqlRetire);

            // executeUpdateメソッドの戻り値は、更新された行数を表します
            int result = pstmt.executeUpdate();
            if (result != 1) {
                return false; // 失敗したら false返す
            }
        } catch (SQLException | ClassNotFoundException e) {
            // データベース接続やSQL実行失敗時の処理
            // JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return false; // 失敗したら false返す
        } finally {
            // PrepareStatementインスタンスのクローズ処理
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return false; // 失敗したら false返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return false; // 失敗したら false返す
                }
            }
        }
        return true;
    }

    public EmployeeBean findEmpBean(String employeeId) {
        EmployeeBean empBean = null; // nullはスタック領域に代入してる。何も指し示していない。(参照してるものがない状態)

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(DRIVER_NAME); // JDBCドライバを読み込み
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS); //
            String sql = "select * from employees where employeeId = ?"; // SELECT文を準備
            pstmt = conn.prepareStatement(sql); // 準備したSQLをデータベースに届けるPrepareStatementインスタンスを取得する
            pstmt.setString(1, employeeId);
            // SQLを実行し、結果はResultSetインスタンスに格納される
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int gender = rs.getInt("gender");
                int photoId = rs.getInt("photoId");
                String zipNumber = rs.getString("zipNumber");
                String pref = rs.getString("pref");
                String address = rs.getString("address");
                String departmentId = rs.getString("departmentId");
                // 結果セットから取得できるのは java.sql.Date型のオブジェクトです
                java.sql.Date sqlHire = rs.getDate("hireDate");
                java.sql.Date sqlRetire = rs.getDate("retirementDate");
                // 入社日を、java.util.Date型に変換する 入社日は、nullではない
                java.util.Date hireDate = new java.util.Date(sqlHire.getTime());
                // 退社日を、java.util.Date型に変換する 退社日は、nullありうるので、nullを変換するとエラー発生する
                java.util.Date retirementDate = null; // 結果セットから 取得したsqlRetire が nullだったら、java.util.Date型でも nullにする
                if (sqlRetire != null) { // nullじゃないなら java.util.Date型に変換する
                    retirementDate = new java.util.Date(sqlRetire.getTime());
                }
                // 引数ありのコンストラクタをよぶ newでピープ領域メモリ確保
                empBean = new EmployeeBean(employeeId, name, age, gender, photoId, zipNumber, pref, address,
                        departmentId, hireDate, retirementDate);
            }
        } catch (SQLException | ClassNotFoundException e) {
            // データベース接続やSQL実行失敗時の処理
            // JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return null; // 失敗した時に、nullを返す
        } finally {
            // ResultSetインスタンスのクローズ処理
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗した時に、nullを返す
                }
            }
         // PrepareStatementインスタンスのクローズ処理
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗した時に、nullを返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗した時に、nullを返す
                }
            }
        }
        return empBean;
    }

}
