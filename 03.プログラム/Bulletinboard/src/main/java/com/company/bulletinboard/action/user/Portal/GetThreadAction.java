package com.company.bulletinboard.action.user.Portal;

// 利用者ポータルにスレッド一覧を表示する処理
// ・getAllThreadsメソッドにてスレッド情報を取得する
// ・PostDaoクラスの「getPostCountByThreadId」メソッドを呼び出して、
// 　各スレッドに対して投稿件数を取得し、それをスレッドオブジェクトに格納する。

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.dao.PostDao;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * スレッド一覧表示Actionクラス
 */
public class GetThreadAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(GetThreadAction.class);

	// 画面表示で使用するスレッドのリスト
	private List<User> threads = new ArrayList<>();

	// 投稿カウント用のフィールド
	private int post_count;

	// PostDaoクラスのインスタン生成
	private PostDao postDao = new PostDao();

	// 投稿カウント用フィールドのゲッター
	public int getPost_count() {
		return post_count;
	}

	// 投稿カウント用フィールドのセッター
	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

	/**
	 * メイン処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
	@Override
	public String mainProc() throws Exception {
		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");

		if (sessionUser == null) {
			addActionError("User session is missing.");
			return ERROR;
		} else {
			// ログにユーザー情報を出力する
			logger.info("Session User: " + sessionUser.getUser_name());
		}

		// 開始ログ出力
		logger.info("GetThreadAction: mainProc() start");

		// 画面表示で使用するスレッドのリストを取得
		getAllThreads();

		// 終了ログ出力
		logger.info("GetThreadAction: mainProc() end");

		// 各スレッドに対して投稿件数を取得し、それをスレッドオブジェクトに格納する。
		// これにより投稿件数が、JSPファイルでの表示が可能になる。
		for (User thread : threads) {
			int postCount = postDao.getPostCountByThreadId(thread.getThread_id());
			thread.setPost_count(postCount);
			logger.debug("Set post count for thread_id " + thread.getThread_id() + ": " + postCount);
		}

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * 利用者ポータルのスレッド一覧に表示するスレッド情報をDBから取得する
	 * @throws SQLException 
	 */
	public void getAllThreads() throws Exception {

		// SQLを生成
		String sql = "SELECT * FROM threads";

		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			// クエリ結果をresultSetに保存
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// 古いデータをクリアする
				threads.clear();

				// resultSetから取得結果をUserクラスにセット
				while (resultSet.next()) {
					// インスタンスを生成
					User thread = new User();

					thread.setThread_id(resultSet.getInt("thread_id"));
					thread.setThread_title(resultSet.getString("thread_title"));
					thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
					thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

					// 'thread'オブジェクトに入っている各値を、「threads」リストに保存する
					threads.add(thread);

					// デバッグログ
					System.out.println("thread_id: " + thread.getThread_id());
					System.out.println("thread_title: " + thread.getThread_title());
					System.out.println("thread_delete_flag: " + thread.getThread_delete_flag());
					System.out.println("thread_delete_day: " + thread.getThread_delete_day());

					// デバッグ処理
					logger.debug("thread_id = " + thread.getThread_id());
					logger.debug("thread_title = " + thread.getThread_title());
					logger.debug("thread_delete_flag = " + thread.getThread_delete_flag());
					logger.debug("thread_delete_day = " + thread.getThread_delete_day());

				}

				logger.debug("Resources are about to be closed: " + connection);

			}

		} catch (SQLException e) {
			logger.error("Error closing connection", e);
		}

	}

	// 「threads」リストをuserPortal.jspにリターンする
	public List<User> getThreads() {
		return threads;
	}

}