package com.company.bulletinboard.action.user.Portal;

// 該当ユーザーの投稿データをDBテーブルから削除する処理
// 後続の処理(PostListActionクラスの投稿一覧取得処理)でスレッドIDを共有する為、意図的にセッションに保存

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 * 投稿削除用のアクション
 * @throws SQLException 
 */
public class DeletePostAction extends BaseAction implements SessionAware {

	// DeletePostAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(DeletePostAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	//  Userモデルをインスタンス化
	private User post = new User();

	//  取得したidを保持するため、インスタンス変数 「post_id」を定義
	private int post_id;

	// スレッドIDを保持する為のフィールド
	private int thread_id;

	// post_id用の、セッターメソッド
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	// post_id用の、ゲッターメソッド
	public int getPost_id() {
		return post_id;
	}

	//postビーンオブジェクトのプロパティに外部からアクセスできるようにする為の、ゲッターメソッド。
	public User getPost() {
		return post;
	}

	// thread_id用のゲッターメソッド
	public int getThread_id() {
		return thread_id;
	}

	// thread_id用のセッタメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	/**
	 * メイン処理
	 * 投稿削除メソッド
	 * @throws SQLException 
	 */
	@Override
	public String mainProc() throws Exception {

		// 開始ログ出力
		logger.info("DeletePostAction: mainProc() start");

		// セッションに格納されているthread_idの値をInteger型の「threadId」変数に代入
		Integer threadId = (Integer) session.get("thread_id");

		// 現在のセッション情報をデバック表示
		logger.debug("Session contents: " + session);

		// threadIdの値が空で無ければ、threadIdの値を、クラスフィールドの「thread_id」に代入する
		// クラス内でセッションから送られて来た、スレッドIDが使用できるようにする。
		if (threadId != null) {
			logger.debug("Thread ID from session: " + threadId);
			this.thread_id = threadId;
		} else {
			logger.error("Thread ID not found in session");
			addActionError("Thread ID not found.");
			return ERROR;
		}

		// SQLを生成
		String sql = "DELETE FROM posts WHERE post_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			// プレースホルダ1にidをバインド
			ps.setInt(1, post_id);

			// rowsAffectedをチェックし、削除が成功したかどうかを確認するロジック
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {

				// 削除成功ログ
				logger.info("Post with post_id " + post_id + " deleted successfully.");
				logger.info("EditPostAction: mainProc() end");

				// 後続の処理(投稿編集/削除)でスレッドIDを共有する為、意図的にセッションに保存します。
				// この処理が無いとスレッドIDが後続の処理に渡れないことが判明。アクション間の連携ができない(2024/08/01追記)
				session.put("thread_id", this.thread_id);

				return SUCCESS;
			} else {
				addActionError("No post found with the provided post_id.");
				return ERROR;
			}

		} catch (SQLException e) {
			// ログにエラーを出力し、エラーを返す		
			logger.error("接続を閉じる際にエラーが発生しました: ", e);
			addActionError("No user found with the provided bulletinboard_id.");
			return ERROR;
		}

	}

}
