package com.company.bulletinboard.action.user.Portal;

// スレッド作成時のDB登録処理
// スレッド作成画面のスレッド情報と、掲示板IDを取得し保持する。合わせてセッションからユーザーIDを取得する。
// 取得した情報を元に、スレッド登録処理を実効する。
// 処理成功時にSUCCESSの文字列を返す

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertThreadAction extends BaseAction {

	// 掲示板IDを保持するフィールド
	private int bulletinboard_id;

	// スレッドのフォームデータを保持するUserモデルオブジェクト
	private User thread = new User();

	// デバッグ用のログ
	private static final Logger logger = LogManager.getLogger(InsertThreadAction.class);

	// ゲッターメソッド: 掲示板IDを取得
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// セッターメソッド: 掲示板IDを設定
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	// ゲッターメソッド: スレッド情報を取得
	public User getThread() {
		return thread;
	}

	// セッターメソッド: スレッド情報を設定
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
	public String mainProc() {
		logger.info("InsertThreadAction: mainProc開始");
		
		// バリデーションにエラーがある場合、入力画面に戻る
		if (hasFieldErrors()) {
			return INPUT;
		}

		// セッションからユーザー情報を取得
		// "loggedInUser"キーで保存された認証済みユーザーデータをUserオブジェクトに取得
		User sessionUser = (User) session.get("loggedInUser");

		// セッションの確認
		if (sessionUser == null || sessionUser.getUser_id() <= 0) {
			addActionError("Invalid user ID: " + (sessionUser != null ? sessionUser.getUser_id() : "null"));
			return ERROR;
		}

		// セッションユーザーのIDをthreadオブジェクトにセット
		thread.setUser_id(sessionUser.getUser_id());

		// 掲示板IDをthreadオブジェクトにセット
		thread.setBulletinboard_id(getBulletinboard_id());

		// デバッグ処理
		logger.debug("Thread Bulletinboard ID: " + thread.getBulletinboard_id());
		logger.debug("Thread User ID: " + thread.getUser_id());

		try (Connection connection = DatabaseConnectionManager.getConnection()) {

			// 掲示板IDのバリデーション
			if (!isValidBulletinboardId(thread.getBulletinboard_id(), connection)) {
				addActionError("Invalid bulletinboard ID: " + thread.getBulletinboard_id());
				return ERROR;
			}

			// 
			String sql = "INSERT INTO threads (thread_title, bulletinboard_id, user_id, thread_delete_flag, thread_delete_day) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement ps = connection.prepareStatement(sql)) {

				// スレッドオブジェクトのデータをプリペアードステートメントにバインド
				ps.setString(1, thread.getThread_title());
				ps.setInt(2, thread.getBulletinboard_id());
				ps.setInt(3, thread.getUser_id());
				ps.setInt(4, thread.getThread_delete_flag());
				ps.setString(5, thread.getThread_delete_day());

				// データベースへの登録処理
				int rowsAffected = ps.executeUpdate();
				if (rowsAffected > 0) {
					logger.info("Thread created successfully. Thread Title: " + thread.getThread_title());
					return SUCCESS;
				} else {
					addActionError("Failed to create thread.");
					return ERROR;
				}
			}
		} catch (SQLException e) {
			logger.error("Database connection error", e);
			addActionError("Database error: " + e.getMessage());
			return ERROR;
		}
	}

	// 掲示板IDが有効かどうかを確認するメソッド
	private boolean isValidBulletinboardId(int bulletinboardId, Connection connection) throws SQLException {
		String sql = "SELECT COUNT(*) FROM bulletinboard WHERE bulletinboard_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, bulletinboardId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}
}
