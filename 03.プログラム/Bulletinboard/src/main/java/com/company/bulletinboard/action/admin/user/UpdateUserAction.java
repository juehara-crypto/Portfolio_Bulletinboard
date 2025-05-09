package com.company.bulletinboard.action.admin.user;

// ユーザー編集フォームより、ユーザー編集後のDB更新処理

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * ユーザー編集用のアクション(変更したデータをDBに更新処理)
 * @throws SQLException 
 */
public class UpdateUserAction extends BaseAction {

	// EditThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(UpdateUserAction.class);

	// フォームからのデータを受け取るための準備として、'user'フィールドを生成
	// 'user'フィールドは、クラス全体のメソッド等から利用される
	private User user = new User();

	// Viewのフォームから直接、userビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getUser() {
		return user;
	}

	// フォームから送信されたデータを受信するメソッド。
	// メソッド引数の'User user'は、一時的にフォームデータを保持するためのオブジェクト。
	// 'this.user'は、クラス全体で使用されるフィールドであり、
	// 'this.user = user;'により、引数の'user'に保持されたデータがクラスフィールドの'user'に保存される。
	public void setUser(User user) {
		this.user = user;
	}

	// ユーザー削除日に関するバリデーション
	// validate()メソッドは、Struts2のActionSupportクラスで定義されているメソッド。アクションの実行前に、このメソッドが呼び出され、
	// バリデーションロジックがここに記述される。ここでは、ユーザー削除日に関するバリデーションが行われる。
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

		// if (user.getUser_name() != null && !user.getUser_name().trim().isEmpty()) {
		if (user.getUser_name() == null || user.getUser_name().trim().isEmpty()) {

		// 空チェックはすでにされているので処理をスキップ
		} else {
			// ユーザー名の長さが有効な範囲内であれば、特殊文字バリデーションを実行
			if (!validateUsername(user.getUser_name())) {
				addFieldError("user.user_name", "ユーザー名には次の特殊文字を含めることはできません: @, #, $, %, &, *");
			}
		}

		
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
		
		// 空チェックはすでにされているので処理をスキップ
		} else if (user.getPassword().length() < 8 || user.getPassword().length() > 64) {

		} else if (!validatePassword(user.getPassword())) {
			addFieldError("user.password", "パスワードには英字、数字、記号を含めてください。");
		}

		// ユーザー削除日のバリデーション
		// まず、削除日がnullや空白でないことを確認し、次にその日付を指定のフォーマットに基づいて解析する。
		if (user.getDelete_day() != null && !user.getDelete_day().trim().isEmpty()) {
			try {

				// dateFormat.parse()で日付をパースし、過去の日付（現在の日時より前の日付）かどうかをbefore()メソッドで確認
				// もし過去の日付であれば、エラーメッセージを追加
				// parse()で例外が発生した場合、例えば無効な形式の日付が入力されたときには、もう一つのエラーメッセージを追加
				parsedDeletionDate = dateFormat.parse(user.getDelete_day());
				if (parsedDeletionDate.before(new Date())) {
					addFieldError("user.delete_day", "ユーザー削除日は未来の日付を指定してください。");
				}
			} catch (Exception e) {
				// addFieldError()メソッドは、指定されたフィールド（ここではuser.delete_day）に対して
				// エラーメッセージを設定。
				// のメソッドにより、エラーが発生した場合に対応するフィールドにエラーメッセージが表示されるようになる。
				addFieldError("user.delete_day", "ユーザー削除日には有効な日付を入力してください。（形式: yyyy-MM-dd HH:mm:ss）");
			}

		}

		// 必須項目チェックなど、フォームデータがバリデーションエラーかどうかを確認
		if (hasFieldErrors()) {
			// バリデーションエラーが発生した場合のログ出力と処理
			logger.error("バリデーションエラーが発生しました: {}", getFieldErrors());
			// return INPUT;
		}
	}

	// ユーザー名のバリデーション(特殊文字)
	// ユーザー名が「null」でなく、指定された特殊文字（@, #, $, %, &, *, !, ?, /, \, =, +, ^, |, <, >, {, }, [, ], ~）を一切含んでいないかをチェック
	// 「特殊文字を含まない」場合に true を返し、ログイン認証処理に進む
	public boolean validateUsername(String username) {
		return username != null && username.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
	}

	// パスワードのバリデーション(英字、数字、記号)
	// まずパスワードがnullでは無いことを確認する。これにより、passwordが未入力（null）の状態で正規表現のチェックを行わないようにしている。
	// matches()メソッドは、パスワードが特定のパターンに一致するかをチェックする。
	// (?=.*[a-zA-Z])にて、パスワードの中に少なくとも1つ以上の英字が含まれているかを確認
	// (?=.*\\d)にて、パスワードの中に少なくとも1つ以上の数字が含まれているかを確認
	// (?=.*[\\W_])にて、少なくとも1文字の記号が含まれているか（\\W は英数字以外の文字を指し、_ も含む）を確認
	// よってパスワードに「英字、数字、記号」が含んでいる場合は、true を返し、ログイン認証処理に進む
	public boolean validatePassword(String password) {
		return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).*$");
	}

	/**
	 * メイン処理
	 * ユーザー更新処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
	@Override
	public String mainProc() throws Exception {

		// SQLを生成
		String sql = "UPDATE users SET user_name = ?, password = ?, auth_type = ?, delete_flag = ?, delete_day = ? WHERE user_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			// 各プレースフォルダと、userオブジェクトのプロパティをバインド
			ps.setString(1, user.getUser_name());
			ps.setString(2, user.getPassword());
			ps.setInt(3, user.getAuth_type());
			ps.setInt(4, user.getDelete_flag());
			ps.setString(5, user.getDelete_day());
			ps.setInt(6, user.getUser_id());

			// デバッグログ
			System.out.println("User ID: " + user.getUser_id());
			System.out.println("User Name: " + user.getUser_name());
			System.out.println("Password: " + user.getPassword());
			System.out.println("Auth Type: " + user.getAuth_type());
			System.out.println("Delete Flag: " + user.getDelete_flag());
			System.out.println("Delete Day: " + user.getDelete_day());

			// デバッグログ
			logger.debug("User ID: = " + user.getUser_id());
			logger.debug("User Name: = " + user.getUser_name());
			logger.debug("Password: = " + user.getPassword());
			logger.debug("Auth Type: = " + user.getAuth_type());
			logger.debug("Delete Flag: = " + user.getDelete_flag());
			logger.debug("Delete Day: = " + user.getDelete_day());

			// SQLクエリを実行し、更新された行数を取得
			int rowsAffected = ps.executeUpdate();

			// 更新された行数が1以上であることを確認
			if (rowsAffected > 0) {
				// 更新が成功した場合、ログに成功メッセージを記録
				logger.info("User updated successfully. User ID: " + user.getUser_id());
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