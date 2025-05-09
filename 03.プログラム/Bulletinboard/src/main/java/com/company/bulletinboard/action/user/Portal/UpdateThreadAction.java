
package com.company.bulletinboard.action.user.Portal;

//スレッド編集画面のフォームからデータを取得し、DBテーブルのアップデートを行う
//後続の処理((PostListActionクラスの投稿一覧取得処理)でスレッドIDを共有する為、意図的にセッションに保存する。

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

public class UpdateThreadAction extends BaseAction implements SessionAware {

	// UpdateThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(UpdateThreadAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 掲示板IDを保持する為のフィールド
	private int bulletinboard_id;

	// スレッドIDを保持する為のフィールド
	private int thread_id;

	// ユーザーIDを保持する為のフィールド
	private int user_id;

	// bulletinboard_id用のゲッターメソッド
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// bulletinboard_id用のセッターメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
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

	// user_id用のセッタメソッド
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	// フォームからのデータを受け取るための準備として、'thread'フィールドを生成
	// 'thread'フィールドは、クラス全体のメソッド等から利用される
	private User thread = new User();

	// Viewのフォームから直接、threadビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getThread() {
		return thread;
	}

	// フォームから送信されたデータを受信するメソッド。
	// メソッド引数の'User Thread'は、一時的にフォームデータを保持するためのオブジェクト。
	// 'this.thread'は、クラス全体で使用されるフィールドであり、
	// 'this.thread = thread;'により、引数の'thread'に保持されたデータがクラスフィールドの'thread'に保存される。
	public void setThread(User thread) {
		this.thread = thread;
	}

	// XMLファイル（InsertThreadAction-validation.xml）によるバリデーションに加え、さらに詳細な検証を行う処理。
	// validate()メソッドは、Struts2のActionSupportクラスで定義されているメソッド。アクションの実行前に、このメソッドが呼び出され、
	// バリデーションロジックがここに記述される。ここでは、スレッドタイトルに関するバリデーションが行われる。
	@Override
	public void validate() {

		// スレッドタイトルが空でない場合にのみ特殊文字のバリデーションを行う
		// まず、タイトルがnullでなく、空白でないことを確認する。
		if (thread.getThread_title() != null && !thread.getThread_title().trim().isEmpty()) {

			// 次に、validateThreadTitle()が使われ、特殊文字が含まれていないかを確認。
			// もし不正な文字が含まれていた場合、addFieldError()メソッドによってエラーメッセージが追加される。
			// これにより、ビュー側でバリデーションメッセージが表示される。
			if (!validateThreadTitle(thread.getThread_title())) {
				addFieldError("thread.thread_title", "スレッドタイトルには次の特殊文字を含めることはできません: @, #, $, %, &, *");
			}
		}

	}

	// スレッドタイトルの特殊文字チェック
	// title が null ではなく、かつ特殊文字（@, #, $, など）が含まれていない場合に true を返す。それ以外の場合には false を返す。
	// まず、title が null ではないか（つまり、値が存在しているか）を確認。
	// title.matches(...)にて、title が指定された正規表現（パターン）に一致するかどうかをチェック。ここでは、特殊文字が含まれていないかを確認。
	// 以下の正規表現は[] 内に含まれる文字を「含まない」ことを指定。このパターンに一致する文字列は、1文字以上であり、かつ上記の特殊文字を一切含まないことを要求。
	// 
	public boolean validateThreadTitle(String title) {
		return title != null && title.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
	}

	@Override
	public String mainProc() throws Exception {

		// バリデーションにエラーがある場合、入力画面に戻る
		if (hasFieldErrors()) {
			return INPUT;
		}

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

		// SQLを生成
		String sql = "UPDATE threads SET thread_title = ? WHERE thread_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			// 各プレースフォルダと、threadオブジェクトのプロパティをバインド
			ps.setString(1, thread.getThread_title());
			ps.setInt(2, thread.getThread_id());

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

			// SQLクエリを実行し、更新された行数を取得
			int rowsAffected = ps.executeUpdate();

			// 更新された行数が1以上であることを確認
			if (rowsAffected > 0) {
				// 更新が成功した場合、ログに成功メッセージを記録
				logger.info("Thread updated successfully. Thread ID: " + thread.getThread_id());

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