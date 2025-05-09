
package com.company.bulletinboard.action.user.Portal;

// このクラスは、スレッドIDに紐づくクエリ結果を取得する。
// ThreadDetailActionからgetThreadServiceByIdメソッドが呼び出され、結果を返す

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadServiceById {

	// ThreadServiceByIdのロガーインスタンス生成
	private static final Logger logger = LogManager.getLogger(ThreadServiceById.class);

	//  IDに基づいてスレッド情報を取得するメソッド。データベース接続を確立し、クエリを実行して結果を User オブジェクトに設定
	public User getThreadServiceById(int id) throws Exception {
		logger.info("ThreadServiceById: mainProc() start");
		logger.info("Fetching thread details for ID: " + id);

		User thread = null;

		String query = "SELECT * FROM threads WHERE thread_id = ?";

		// コネクション取得と、ステートメントを準備
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			// 一番目のステートメントの引数に「id」を紐づける
			preparedStatement.setInt(1, id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {

					// User型のオブジェクトを生成し、ResultSetの中身を代入する
					thread = new User();
					thread.setThread_id(resultSet.getInt("thread_id"));
					thread.setThread_title(resultSet.getString("thread_title"));
					thread.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					thread.setUser_id(resultSet.getInt("user_id"));
					thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
					thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

					// ロギング
					logger.debug("thread_id: = " + thread.getThread_id());
					logger.debug("thread_title: = " + thread.getThread_title());
					logger.debug("bulletinboard_id: = " + thread.getBulletinboard_id());
					logger.debug("user_id: = " + thread.getUser_id());
					logger.debug("thread_delete_flag: = " + thread.getThread_delete_flag());
					logger.debug("thread_delete_day: = " + thread.getThread_delete_day());

					// デバッグログを標準出力
					System.out.println("Thread_id: " + thread.getThread_id());
					System.out.println("Thread_title: = " + thread.getThread_title());
					System.out.println("bulletinboard_id: = " + thread.getBulletinboard_id());
					System.out.println("user_id: = " + thread.getUser_id());
					System.out.println("thread_delete_flag: = " + thread.getThread_delete_flag());
					System.out.println("thread_delete_day: = " + thread.getThread_delete_day());

					logger.info("BThreadServiceById: mainProc() end");
				}
			}

		} catch (SQLException e) {
			// エラー発生時のログ出力とエラーメッセージ設定
			logger.error("接続を閉じる際にエラーが発生しました: ", e);

		}

		// Userオブジェクトを返す
		return thread;
	}
}
