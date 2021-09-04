package dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PhotoDAO {

    final String DRIVER_NAME = "org.postgresql.Driver";
    final String JDBC_URL = "jdbc:postgresql://localhost:5432/emp01";
    final String DB_USER = "postgres";
    final String DB_PASS = "postgres";

    /**
     * アップロードした画像のファイルデータをphotosテーブルへ新規登録し、そのphotoIdが返る photoIdに 0 が入ってたら、失敗
     * @param photoData
     * @param mime
     * @return photoId
     */
    public int photoDataAdd(byte[] photoData, String mime) {
        int photoId = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // ?::bytea  PostgreSQL だと、 ?::integer  ?::status ?::bytea とする 文字列だけ ? だけでいい
            String sql = "insert into photos (photoData, mime) values (?::bytea, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setBinaryStream(1, new ByteArrayInputStream(photoData));
            pstmt.setString(2, mime);

            int result = pstmt.executeUpdate(); // 更新処理 戻り値は、変更したレコードの数
            if (result != 1) { // 失敗
                return photoId; // 初期値の 0を返す、呼び出し元では、0だったら、失敗したとする
            } else {
                // getGeneratedKeys()メソッドは、このStatementオブジェクトを実行した結果として作成された自動生成キーを取得します。
                // このStatementオブジェクトがキーを生成しなかった場合は、空のResultSetオブジェクトが返されます
                rs = pstmt.getGeneratedKeys(); // 結果セットに、自動生成キーを取得だけ取得したい 今回は、他は要らないので
                if (rs.next()) { // １データしか、挿入しないから、while ではなくて if でいい
                    photoId = rs.getInt(1); // 結果セットの仮のテーブルの先頭のカラムを指定してる オートインクリメントの値
                    // 結果セットには、getGeneratedKeys() で、自動生成キーを取得だけ取得してるから１つのカラムだけがある
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0; // エラー発生した時は、0 の値を返してる 呼び出し元で、0 が入ってたら、エラーだとする
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return 0; // close()で エラー発生した時は、0 の値を返してる
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return 0;// close()で エラー発生した時は、0 の値を返してる
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return 0;// close()で エラー発生した時は、0 の値を返してる
                }
            }
        }
        return photoId;
    }

    /**
     * 写真IDから写真データを取得する
     * @param photoId
     * @return byte[] data
     */
    public byte[] getPhotoData(int photoId) {
        byte[] data = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // PostgreSQL では、プレースホルダーに ?::integer ?::bytea ?::status など付けます。 文字列は ? だけ
            String sql = "select photoData from photos where photoId = ?::integer";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, photoId);
            rs = pstmt.executeQuery();
            if (rs.next()) { // １つのデータしかないから、whileじゃなくて if
                data = rs.getBytes("photoData"); //引数 1 でもいい photoDataカラムのデータしか結果セットにないから
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // 失敗したら、nullを返します
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;// 失敗したら、nullを返します
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;// 失敗したら、nullを返します
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;// 失敗したら、nullを返します
                }
            }
        }
        return data;
    }

    /**
     * photoId から mimeタイプを取得する
     * @param photoId
     * @return String mime
     */
    public String getMime(int photoId) {
        String mime = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            String sql = "select mime from photos where photoId = ?::integer";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, photoId);
            rs = pstmt.executeQuery(); // 結果セットには、１データだけ返る
            if (rs.next()) { // １データだけだから、whileじゃなくて if でいい
                mime = rs.getString("mime"); // 結果セットには１つのカラム mime しかないから 引数は 1 でもいい
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // 失敗したら nullを返す
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら nullを返す
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら nullを返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら nullを返す
                }
            }
        }
        return mime;
    }

}
