package com.company.bulletinboard.action.user.Portal;

// スレッド編集画面で既存データをDBから取得し、取得したデータをviewに渡す処理
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

import com.opensymphony.xwork2.ActionContext;

import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
* スレッド編集用のアクション(既存データをフォームに表示)
* @throws SQLException 
*/
public class EditThreadAction extends BaseAction implements SessionAware {

	// EditThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(EditThreadAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	//  threadのデータ領域を宣言
	private User thread;

	//  取得したthread_idを保持するため、インスタンス変数 「thread_id」を定義
	private int thread_id;

	//　掲示板IDを保持する為のフィールド
	private int bulletinboard_id;

	// ユーザーIDを保持する為のフィールド
	private int user_id;

	// フォームから取得したthread_idと、インスタンス変数「thread_id 」との紐づけ用の、セッターメソッド
	public void setThread_id_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// Viewやその他の処理から個別にpost_idにアクセスさせる為のメソッド
	public int getThread_id() {
		return thread_id;
	}

	// Viewのフォームから直接、ビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getThread() {
		return thread;
	}

	// threadオブジェクトアクセス用のセッターメソッド
	public void setThread(User thread) {
		this.thread = thread;
	}

	// bulletinboard_id用のゲッターメソッド
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// bulletinboard_id用のセッターメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	// user_id用のゲッターメソッド
	public int getUser_id() {
		return user_id;
	}

	// user_id用のセッタメソッド
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	// getThreadById()メソッドの戻り値を、mainProcの戻り値として返している。 
	// この戻り値は、表示するビューを決定するために使用する
	@Override
	public String mainProc() throws Exception {
		// 開始ログ出力
		logger.info("EditThreadAction: mainProc() start");

		// セッションの格納されているthread_idの値をInteger型の「threadId」変数に代入
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
		getThreadById(thread_id);

		// 終了ログ出力
		logger.info("EditThreadAction: mainProc() end");

		// オブジェクトをリクエストまたはセッションに設定
		ActionContext.getContext().getSession().put("thread", thread);

		// デバッグログで確認
		if (thread != null) {
			System.out.println("Thread object saved in session: " + thread.getUser_id());
		} else {
			System.out.println("Thread object is null");
		}

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * DBに接続し、取得したスレッドIDから既存の投稿データを取得
	 * @throws SQLException 
	 */
	public String getThreadById(int thread_id) throws Exception {

		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");

		// SQLを生成
		String sql = "SELECT * FROM threads WHERE thread_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			//準備されたステートメントに対して、最初のパラメータとして「thread_id」の値を設定する
			preparedStatement.setInt(1, thread_id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装済み
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// resultSetから取得結果をUserクラスにセット
				if (resultSet.next()) {
					// Userモデルをインスタン化する
					thread = new User();

					// resultSetの値をUserオブジェクトに代入
					thread.setThread_id(resultSet.getInt("thread_id"));
					thread.setThread_title(resultSet.getString("thread_title"));
					thread.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					thread.setUser_id(resultSet.getInt("user_id"));
					thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
					thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

					// デバッグログを標準出力
					System.out.println("thread_id: " + thread.getThread_id());
					System.out.println("thread_title: " + thread.getThread_title());
					System.out.println("Bulletinboard_id: " + thread.getBulletinboard_id());
					System.out.println("User ID: " + thread.getUser_id());
					System.out.println("thread_delete_flag: " + thread.getThread_delete_flag());
					System.out.println("thread_delete_day: " + thread.getThread_delete_day());

					// デバッグログ
					logger.debug("thread_id: = " + thread.getThread_id());
					logger.debug("thread_title: = " + thread.getThread_title());
					logger.debug("Bulletinboard_id: = " + thread.getBulletinboard_id());
					logger.debug("user_id: = " + thread.getUser_id());
					logger.debug("thread_delete_flag: = " + thread.getThread_delete_flag());
					logger.debug("thread_delete_day: = " + thread.getThread_delete_day());

					// セッションにスレッドIDを保存
					session.put("thread_id", this.thread_id);

					// 「SUCCESS」の文字列を返す
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