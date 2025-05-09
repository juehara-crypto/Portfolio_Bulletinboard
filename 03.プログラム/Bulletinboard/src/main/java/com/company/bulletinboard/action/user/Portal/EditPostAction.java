package com.company.bulletinboard.action.user.Portal;

// 投稿編集画面で既存データをDBから取得し、取得したデータをviewに渡す処理
// 後続の処理(PostListActionクラスの投稿一覧取得処理)でスレッドIDを共有する為、意図的にセッションに保存

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.company.bulletinboard.action.user.Portal.UpdatePostAction;

import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 * 投稿編集用のアクション(既存データをフォームに表示)
 * @throws SQLException 
 */
public class EditPostAction extends BaseAction implements SessionAware {

	// EditPostAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(EditPostAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// postのデータ領域を宣言
	private User post;

	//  取得したpost_idを保持するため、インスタンス変数 「post_id」を定義
	private int post_id;

	// スレッドIDを保持する為のフィールド
	private int thread_id;

	private int user_id;

	// フォームから取得したpost_idと、インスタンス変数「post_id 」との紐づけ用の、セッターメソッド
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	// Viewやその他の処理から個別にpost_idにアクセスさせる為のメソッド
	public int getPost_id() {
		return post_id;
	}

	// Viewのフォームから直接、postビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
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

	// user_id用のゲッターメソッド
	public int getUser_id() {
		return user_id;
	}

	// user_id用ののセッタメソッド
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	// getPostById()メソッドの戻り値を、mainProcの戻り値として返している。 
	// この戻り値は、表示するビューを決定するために使用する
	@Override
	public String mainProc() throws Exception {
		// 開始ログ出力
		logger.info("EditPostAction: mainProc() start");

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

		// メソッド呼び出し
		getPostById(post_id);

		// 終了ログ出力
		logger.info("EditPostAction: mainProc() end");

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * DBに接続し、取得したIDから既存の投稿データを取得
	 * @throws SQLException 
	 */
	public String getPostById(int post_id) throws Exception {

		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");

		// SQLを生成
		String sql = "SELECT * FROM posts WHERE post_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			//準備されたステートメントに対して、最初のパラメータとして「post_id」の値を設定する
			preparedStatement.setInt(1, post_id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装済み
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// resultSetから取得結果をUserクラスにセット
				if (resultSet.next()) {
					// Userモデルをインスタン化する
					post = new User();

					// resultSetの値をUserオブジェクトに代入
					post.setPost_id(resultSet.getInt("post_id"));
					post.setPost_content(resultSet.getString("post_content"));
					post.setThread_id(resultSet.getInt("thread_id"));
					post.setUser_id(resultSet.getInt("user_id"));

					// デバッグログを標準出力
					System.out.println("Post ID: " + post.getPost_id());
					System.out.println("Post Content: " + post.getPost_content());
					// System.out.println("Thread ID: " + post.getThread_id());
					System.out.println("User ID: " + post.getUser_id());

					// デバッグログ
					logger.debug("post_id: = " + post.getPost_id());
					logger.debug("post_content: = " + post.getPost_content());
					logger.debug("user_id: = " + post.getUser_id());
					logger.debug("thread_id: = " + thread_id);

					// セッションにスレッドIDを保存
					session.put("thread_id", this.thread_id);

					return SUCCESS;
				} else {
					addActionError("No user found with the post_id.");
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
}
