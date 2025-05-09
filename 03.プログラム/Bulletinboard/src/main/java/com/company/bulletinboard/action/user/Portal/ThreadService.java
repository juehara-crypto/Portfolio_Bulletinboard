package com.company.bulletinboard.action.user.Portal;

// 掲示板IDに紐づくスレッド情報を取得する処理。
// BoardDetailActionクラスから、このクラスのgetThreadsByBulletinboardIdメソッドが呼び出される。
// 掲示板IDに紐づくスレッド情報を取得後、結果をthreadsオブジェクトに入れて BoardDetailActionクラスに返す。

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThreadService {

	// ThreadServiceクラス用のデバック処理。ロガーインスタンスの生成
	private static final Logger logger = LogManager.getLogger(ThreadService.class);

	/**
	 * 掲示板IDに紐づくスレッド一覧を取得
	 *
	 * @param bulletinboardId 掲示板ID
	 * @return スレッドのリスト
	 * @throws Exception データベース操作中にエラーが発生した場合
	 */
	public List<User> getThreadsByBulletinboardId(int bulletinboardId) throws Exception {

		// デバッグ処理の開始ログを記録
		logger.info("ThreadService: mainProc() start");

		// User型のthreadsアレイリストの生成
		List<User> threads = new ArrayList<>();

		// DB接続処理の初期化
		// Connection connection = null;

		// スレッド情報取得用のクエリ処理
		String query = "SELECT * FROM threads WHERE bulletinboard_id = ?";

		// コネクション取得と、ステートメントを準備
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			logger.info("Database connection opened.");

			preparedStatement.setInt(1, bulletinboardId);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {

					// スレッド情報保持用のUserオブジェクトを生成し、値をthreadオブジェクトに保存する
					User thread = new User();
					thread.setThread_id(resultSet.getInt("thread_id"));
					thread.setThread_title(resultSet.getString("thread_title"));
					thread.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					thread.setUser_id(resultSet.getInt("user_id"));
					thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
					thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

					// 取得したスレッド情報を、threadsリストに保存する
					threads.add(thread);

					// デバックログの生成
					logger.debug("Thread_id: ID = " + thread.getThread_id());
					logger.debug("Thread_title: = " + thread.getThread_title());
					logger.debug("Bulletinboard_id: ID = " + thread.getBulletinboard_id());
					logger.debug("User_id: ID = " + thread.getUser_id());
					logger.debug("Thread_delete_flag: = " + thread.getThread_delete_flag());
					logger.debug("Thread_delete_day:  = " + thread.getThread_delete_day());

					// デバッグログの表示
					// System.out.println("thread_id: " + thread.getThread_id());
					// System.out.println("thread_title: " + thread.getThread_title());
					// System.out.println("bulletinboard_id: " + thread.getBulletinboard_id());
					// System.out.println("user_id: " + thread.getUser_id());
					// System.out.println("thread_delete_flag: " + thread.getThread_delete_flag());
					// System.out.println("thread_delete_day: " + thread.getThread_delete_day());

					logger.debug("Thread details: " + thread.toThreadString());

				}
			}

			logger.info("Database connection closed.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		logger.info("ThreadService: getThreadsByBulletinboardId() end");
		// threadsリストを返す
		return threads;
	}
}
