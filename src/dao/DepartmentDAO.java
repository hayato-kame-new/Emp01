package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DepartmentBean;

public class DepartmentDAO {

    /**
     * 部署のリストを取得する 失敗した時、null返す
     * @return departmentLists
     */
    public List<DepartmentBean> findAll() {

        List<DepartmentBean> depList = new ArrayList<DepartmentBean>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/emp01", "postgres", "postgres");
            String sql = "select departmentId, department from department";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String departmentId = rs.getString("departmentId");
                String department = rs.getString("department");
                DepartmentBean depBean = new DepartmentBean(departmentId, department);
                //            	DepartmentBean departmentBean = new DepartmentBean();
                //				departmentBean.setDepartmentId(departmentId);
                //				departmentBean.setDepartment(department);
                depList.add(depBean);
            }
        } catch (SQLException | ClassNotFoundException e) {
            // データベース接続やSQL実行失敗時の処理
            // JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return depList;
    }

}
