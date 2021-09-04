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


}
