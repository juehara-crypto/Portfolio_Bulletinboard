package com.company.bulletinboard.action.user.Portal;

// 投稿編集画面のフォームからデータを取得し、DBテーブルのアップデートを行う
// 後続の処理((PostListActionクラスの投稿一覧取得処理)でスレッドIDを共有する為、意図的にセッションに保存します。
// この処理が無いとスレッドIDが後続の処理に渡れないことが判明。アクション間の連携ができない為(2024/08/01追記)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

import com.company.bulletinboard.action.user.Portal.EditPostAction;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;


public class UpdatePostAction extends BaseAction implements SessionAware {

	// UpdatePostAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(UpdatePostAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 投稿IDを保持する為のフィールド
	private int post_id;

	// スレッドIDを保持する為のフィールド
	private int thread_id;

	// ユーザーIDを保持する為のフィールド
	private int user_id;

	// 投稿ID用のゲッター
	public int getPost_id() {
		return post_id;
	}

	// 投稿ID用のセッター
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	// thread_id用のゲッターメソッド
	public int getThread_id() {
		return thread_id;
	}

	// thread_id用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// user_id用のゲッターメソッド
	public int getUser_id() {
		return user_id;
	}

	// thread_id用のセッタメソッド
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	// フォームからのデータを受け取るための準備として、'post'フィールドを生成
	// 'post'フィールドは、クラス全体のメソッド等から利用される
	private User post = new User();

	// Viewのフォームから直接、postビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getPost() {
		return post;
	}

	// フォームから送信されたデータを受信するメソッド。
	// メソッド引数の'User post'は、一時的にフォームデータを保持するためのオブジェクト。
	// 'this.post'は、クラス全体で使用されるフィールドであり、
	// 'this.post = post;'により、引数の'post'に保持されたデータがクラスフィールドの'post'に保存される。
	public void setPost(User post) {
		this.post = post;
	}

	@Override
	public String mainProc() throws Exception {

		// セッションの格納されているthread_idの値をInteger型の「threadId」変数に代入
		Integer threadId = (Integer) session.get("thread_id");

		// threadIdの値が空で無ければ、threadIdの値を、クラスフィールドの「thread_id」に代入する
	    // クラス内でセッションから送られて来た、スレッドIDが使用できるようにする。
		logger.debug("Session contents: " + session);
		if (threadId != null) {
			logger.debug("Thread ID from session: " + threadId);
			this.thread_id = threadId;
		} else {
			logger.error("Thread ID not found in session");
			addActionError("Thread ID not found.");
			return ERROR;
		}


		// SQLを生成（post_timestampも更新する）
		String sql = "UPDATE posts SET post_content = ?, post_timestamp = CURRENT_TIMESTAMP WHERE post_id = ?";


		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

		// 各プレースフォルダと、postオブジェクトのプロパティをバインド
		ps.setString(1, post.getPost_content());
		ps.setInt(2, post.getPost_id());

		// 標準出力にデバッグログ
		System.out.println("Post Content: " + post.getPost_content());
		System.out.println("thread_id: " + post.getThread_id());
		System.out.println("Post ID: " + post.getPost_id());
		System.out.println("User ID: " + post.getUser_id());
		System.out.println("Delete_flag: " + post.getPost_delete_flag());
		System.out.println("Post_delete_day: " + post.getPost_delete_day());
		System.out.println("Post_timestamp: " + post.getPost_timestamp());

		// デバック処理
		logger.debug("post_content: = " + post.getPost_content());
		logger.debug("thread_id: = " + post.getThread_id());
		logger.debug("post_id: = " + post.getPost_id());
		logger.debug("user_id: = " + post.getUser_id());
		logger.debug("delete_flag: = " + post.getDelete_flag());
		logger.debug("post_delete_day: = " + post.getPost_delete_day());
		//logger.debug("post_timestamp: = " + sqlTimestamp);

		// SQLクエリを実行し、更新された行数を取得
		int rowsAffected = ps.executeUpdate();

		// 更新された行数が1以上であることを確認
		if (rowsAffected > 0) {
			// 更新が成功した場合、ログに成功メッセージを記録
			logger.info("Post updated successfully. Post ID: " + post.getPost_id());
			
                // スレッドIDをセッションに保存する
		session.put("thread_id", this.thread_id);
			
			// SUCCESSの文字列を返す
			return SUCCESS;
		} else {
			addActionError("Update process failed.");
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
