package com.company.bulletinboard.dao;

// ユーザー認証処理
// LoginActionクラスから渡されたユーザー名とパスワードを使ってデータベースからユーザー情報を取得し、
// その情報を Userオブジェクトの 'user' に格納。

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAO {
	private static final Logger logger = LogManager.getLogger(UserDAO.class);

	// User型のデータを返す為、authenticateメソッドの戻り値の型として「User」を指定。
	public static User authenticate(String user_name, String password) {
		User user = null;

		// デバッグ処理開始
		logger.info("UserDAO: authenticate() start");

		logger.debug("データベース接続開始");

		// SQL文を準備
		String sql = "SELECT * FROM users WHERE user_name=? AND password=?";

		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			// プリペアードステートメントにて各値を紐づけて、SQLを実効
			preparedStatement.setString(1, user_name);
			preparedStatement.setString(2, password);

			logger.debug("クエリ実行: " + preparedStatement.toString());

			// クエリの結果をResultSetの「rs」に代入する
			try (ResultSet rs = preparedStatement.executeQuery()) {

				if (rs.next()) {
					logger.debug("ユーザー認証成功");

					// SQLの実行結果保存用に、userオブジェクトを生成
					user = new User();

					// ResultSetに入っている各値をuserオブジェクトに入れる
					user.setUser_id(rs.getInt("user_id"));
					user.setUser_name(rs.getString("user_name"));
					user.setPassword(rs.getString("password"));
					user.setAuth_type(rs.getInt("auth_type"));
					user.setDelete_day(rs.getString("delete_day"));

					// デバッグ用のログ出力
					logger.debug("Userデータ: user_id=" + user.getUser_id() + ", user_name=" + user.getUser_name() +
							", password=" + user.getPassword() + ", auth_type=" + user.getAuth_type() +
							", delete_flag=" + user.getDelete_flag() + ", delete_day=" + user.getDelete_day());

				}

			}
			logger.debug("Resources are about to be closed: " + connection);
		} catch (SQLException e) {
			logger.error("エラー発生: ", e);
			logger.debug("ユーザー認証失敗");
			e.printStackTrace();
			logger.error("接続を閉じる際にエラーが発生しました: ", e);
		}

		// 処理結果をUser型のuserオブジェクとしてリターンする
		return user;
	}

}
