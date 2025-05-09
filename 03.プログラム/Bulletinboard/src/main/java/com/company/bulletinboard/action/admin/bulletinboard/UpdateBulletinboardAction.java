package com.company.bulletinboard.action.admin.bulletinboard;

// 掲示板編集フォームより、掲示板編集後のDB更新処理

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.action.user.Portal.EditThreadAction;
import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * 掲示板編集用のアクション(変更したデータをDBに更新処理)
 * @throws SQLException 
 */
public class UpdateBulletinboardAction extends BaseAction {

	// EditThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(UpdateBulletinboardAction.class);

	// フォームからのデータを受け取るための準備として、'bulletinboard'フィールドを生成
	// 'bulletinboard'フィールドは、クラス全体のメソッド等から利用される
	private User bulletinboard = new User();

	// Viewのフォームから直接、bulletinboardビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getBulletinboard() {
		return bulletinboard;
	}

	// フォームから送信されたデータを受信するメソッド。
	// メソッド引数の'User bulletinboard'は、一時的にフォームデータを保持するためのオブジェクト。
	// 'this.bulletinboard'は、クラス全体で使用されるフィールドであり、
	// 'this.bulletinboard = bulletinboard;'により、引数の'bulletinboard'に保持されたデータがクラスフィールドの'bulletinboard'に保存される。
	public void setBulletinboard(User bulletinboard) {
		this.bulletinboard = bulletinboard;
	}

	// XMLファイル（UpdateBulletinboardAction-validation.xml）によるバリデーションに加え、さらに詳細な検証を行う処理。	
	// validate()メソッドは、Struts2のActionSupportクラスで定義されているメソッド。アクションの実行前に、このメソッドが呼び出され、	
	// バリデーションロジックがここに記述される。ここでは、掲示板タイトルや削除日に関するバリデーションが行われる。	
	@Override
	public void validate() {
		// 日付フォーマットの設定	
		// SimpleDateFormatは日付のフォーマットを指定するためのクラス。	
		// ここでは「yyyy-MM-dd HH:mm:ss」の形式でフォームの入力を期待している。	
		// setLenient(false)により、厳密な形式チェックを行う。	
		// 例えば、「1999-02-29」などの無効な日付を許容しない。	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		Date parsedDeletionDate = null;

		// 掲示板タイトルが空でない場合にのみ特殊文字のバリデーションを行う	
		// まず、タイトルがnullでなく、空白でないことを確認する。	
		if (bulletinboard.getBulletinboard_title() != null
				&& !bulletinboard.getBulletinboard_title().trim().isEmpty()) {
			// 次に、validateBulletinboardTitle()が使われ、特殊文字が含まれていないかを確認。	
			// もし不正な文字が含まれていた場合、addFieldError()メソッドによってエラーメッセージが追加される。	
			// これにより、ビュー側でバリデーションメッセージが表示される。	
			if (!validateBulletinboardTitle(bulletinboard.getBulletinboard_title())) {
				addFieldError("bulletinboard.bulletinboard_title", "掲示板タイトルには次の特殊文字を含めることはできません: @, #, $, %, &, *");
			}
		}

		// 掲示板削除日のバリデーション	
		// まず、削除日がnullや空白でないことを確認し、次にその日付を指定のフォーマットに基づいて解析する。	
		if (bulletinboard.getBulletinboard_delete_day() != null
				&& !bulletinboard.getBulletinboard_delete_day().trim().isEmpty()) {
			try {
				// dateFormat.parse()で日付をパースし、過去の日付（現在の日時より前の日付）かどうかをbefore()メソッドで確認	
				// もし過去の日付であれば、エラーメッセージを追加	
				// parse()で例外が発生した場合、例えば無効な形式の日付が入力されたときには、もう一つのエラーメッセージを追加	
				parsedDeletionDate = dateFormat.parse(bulletinboard.getBulletinboard_delete_day());
				if (parsedDeletionDate.before(new Date())) {
					addFieldError("bulletinboard.bulletinboard_delete_day", "掲示板削除日は未来の日付を指定してください。");
				}
			} catch (Exception e) {
				// addFieldError()メソッドは、指定されたフィールド（ここではbulletinboard.bulletinboard_delete_day）に対して	
				// エラーメッセージを設定。	
				// のメソッドにより、エラーが発生した場合に対応するフィールドにエラーメッセージが表示されるようになる。	
				addFieldError("bulletinboard.bulletinboard_delete_day",
						"掲示板削除日には有効な日付を入力してください。（形式: yyyy-MM-dd HH:mm:ss）");
			}
		}
	}

	// 掲示板タイトルの特殊文字チェック	
	// title が null ではなく、かつ特殊文字（@, #, $, など）が含まれていない場合に true を返す。それ以外の場合には false を返す。	
	// まず、title が null ではないか（つまり、値が存在しているか）を確認。	
	// title.matches(...)にて、title が指定された正規表現（パターン）に一致するかどうかをチェック。ここでは、特殊文字が含まれていないかを確認。	
	// 以下の正規表現は[] 内に含まれる文字を「含まない」ことを指定。このパターンに一致する文字列は、1文字以上であり、かつ上記の特殊文字を一切含まないことを要求。	
	//	
	public boolean validateBulletinboardTitle(String title) {
		return title != null && title.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
	}

	/**
	 * メイン処理
	 * 掲示板更新処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
	@Override
	public String mainProc() throws Exception {
		
		// バリデーションにエラーがある場合、入力画面に戻る
		if (hasFieldErrors()) {
			return INPUT;
		}
		
		
		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");

		if (sessionUser == null) {
			String errorMessage = "User session is missing.";
			addActionError(errorMessage);
			logger.error("Error in MoveBulletinboardManagementAction: " + errorMessage);
			return ERROR;
		  } else {
			// ログにユーザー情報を出力する
			logger.info("Session User: " + sessionUser.getUser_name());
		}
		
		

		// SQLを生成
		String sql = "UPDATE bulletinboard SET bulletinboard_title = ?, bulletinboard_content = ?, bulletinboard_delete_flag = ?, bulletinboard_delete_day = ? WHERE bulletinboard_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection()) {
				
				// 接続が有効かどうかをチェック
				if (connection.isValid(2)) { // タイムアウトは2秒を指定

					// データベース接続情報をログ出力
					DatabaseMetaData metaData = connection.getMetaData();
					String url = metaData.getURL(); // 接続URL
					String host = "不明";
					String port = "不明";

					// URLを"//"で分割し、ホスト:ポート部分を取得
					try {
						String[] splitUrl = url.split("//");
						if (splitUrl.length > 1) {
							String hostPortPart = splitUrl[1].split("/")[0]; // "localhost:3306" 部分を取得
							String[] hostPort = hostPortPart.split(":");
							host = hostPort[0]; // ホスト部分
							if (hostPort.length > 1) {
								port = hostPort[1]; // ポート部分
							}
						}
					} catch (Exception e) {
						logger.error("URL解析中にエラーが発生しました: {}", e.getMessage(), e);
					}

					// デバッグ用ログ
					if (logger.isDebugEnabled()) {
						logger.debug("データベース接続は正常です。ホスト: {}, ポート: {}, データベース: {}",
								host, port, connection.getCatalog());
					}

				} else {
					logger.error("データベース接続が無効です");
					throw new SQLException("Database connection is invalid.");
				}
				
				
				PreparedStatement ps = connection.prepareStatement(sql);

			// 各プレースフォルダと、bulletinboardオブジェクトのプロパティをバインド
			ps.setString(1, bulletinboard.getBulletinboard_title());
			logger.debug("bulletinboard_title: = " + bulletinboard.getBulletinboard_title());
			
			ps.setString(2, bulletinboard.getBulletinboard_content());
			logger.debug("bulletinboard_content: = " + bulletinboard.getBulletinboard_content());
			
			ps.setInt(3, bulletinboard.getBulletinboard_delete_flag());
			logger.debug("bulletinboard_delete_flag: = " + bulletinboard.getBulletinboard_delete_flag());
			
			ps.setString(4, bulletinboard.getBulletinboard_delete_day());
			logger.debug("bulletinboard_delete_day: = " + bulletinboard.getBulletinboard_delete_day());
			
			ps.setInt(5, bulletinboard.getBulletinboard_id());
			logger.debug("bulletinboard_id: = " + bulletinboard.getBulletinboard_id());

			// デバッグログ
			// System.out.println("Bulletinboard_id: " + bulletinboard.getBulletinboard_id());
			// System.out.println("Bulletinboard_title: " + bulletinboard.getBulletinboard_title());
			// System.out.println("Bulletinboard_content: " + bulletinboard.getBulletinboard_content());
			// System.out.println("Bulletinboard_delete_flag: " + bulletinboard.getBulletinboard_delete_flag());
			// System.out.println("Bulletinboard_delete_day: " + bulletinboard.getBulletinboard_delete_day());


			// SQLクエリを実行し、更新された行数を取得
			int rowsAffected = ps.executeUpdate();

			// 更新された行数が1以上であることを確認
			if (rowsAffected > 0) {
				// 更新が成功した場合、ログに成功メッセージを記録
				logger.info(
						"Bulletinboard updated successfully. Bulletinboard_id: " + bulletinboard.getBulletinboard_id());
				return SUCCESS;
			} else {
				addActionError("No user found with the provided ID.");
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


