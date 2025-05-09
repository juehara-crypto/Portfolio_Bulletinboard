package com.company.bulletinboard.action.user.Portal;

// このクラスは、ユーザー個別の投稿に紐づく親スレッドの詳細情報取得する。
// 「UserPostThreadDetailAction」クラスから、getThreadServiceByIdメソッドが呼び出され、結果を返す。

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPostThreadService {

	// Loggerインスタンス生成
	private static final Logger logger = LogManager.getLogger(UserPostThreadService.class);

	// スレッドIDに基づいてスレッド情報を取得するメソッド
	public User getThreadServiceById(int id) throws Exception {
		logger.info("Fetching thread details for ID: " + id);

		// Userモデルのオブジェクトを初期化
		User thread = null;

		// SQLクエリ
		String query = "SELECT * FROM threads WHERE thread_id = ?";

		// コネクションとステートメントをtry-with-resourcesで自動クローズ
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			// SQLのパラメータ設定
			statement.setInt(1, id);

			// クエリを実行し、結果をResultSetに格納
			try (ResultSet resultSet = statement.executeQuery()) {

				// 結果が存在する場合に、ResultSet値をthreadオブジェクトに格納する
				if (resultSet.next()) {
					thread = new User();
					thread.setThread_id(resultSet.getInt("thread_id"));
					thread.setThread_title(resultSet.getString("thread_title"));
					thread.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					thread.setUser_id(resultSet.getInt("user_id"));
					thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
					thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

					// ロギング UserモデルのtoThreadStringメソッドを使用しデバックログを出力
					logger.debug("Thread details: " + thread.toThreadString());

				} else {
					// スレッドが見つからなかった場合
					logger.debug("No thread found for ID: " + id);
				}
			}

		} catch (SQLException e) {
			// エラーログを出力し、例外をスロー
			logger.error("Error fetching thread details", e);
			throw e;
		}
		// threadオブジェクトを返す
		return thread;
	}
}
