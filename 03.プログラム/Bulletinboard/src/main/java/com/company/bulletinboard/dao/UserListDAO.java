package com.company.bulletinboard.dao;

// UserListActionクラスから呼び出され、getAllUsersメソッドにて全てのユーザーデータを取得する
// データ取得後、usersリストに代入し、UserListActionクラスに返す

import com.company.bulletinboard.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserListDAO {

	// ロガーの設定
	private static final Logger logger = LogManager.getLogger(UserListDAO.class);

	//  データベースから取得したデータを「users」リストに保存する処理
	public List<User> getAllUsers() {

		// 「users」アレイリストを定義
		List<User> users = new ArrayList<>();

		// SQLを生成
		String sql = "SELECT * FROM users";

		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			// クエリの結果をResultSetの「resultSet」に代入する
			try (ResultSet rs = preparedStatement.executeQuery()) {

				// ResultSetのデータをuserオブジェクトにセット
				while (rs.next()) {
					User user = new User();
					user.setUser_id(rs.getInt("user_id"));
					user.setUser_name(rs.getString("user_name"));
					user.setPassword(rs.getString("password"));
					user.setAuth_type(rs.getInt("auth_type"));
					user.setDelete_flag(rs.getInt("delete_flag"));
					user.setDelete_day(rs.getString("delete_day"));

					users.add(user);
				}

				logger.debug("Number of users retrieved: " + users.size());
			} catch (SQLException e) {
				logger.error("Error executing query", e);
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// SQLExceptionの処理
			logger.error("Error obtaining database connection", e);
			e.printStackTrace();
		}

		// 全てのUserオブジェクトを含むusersリストを返す
		return users;
	}
}
