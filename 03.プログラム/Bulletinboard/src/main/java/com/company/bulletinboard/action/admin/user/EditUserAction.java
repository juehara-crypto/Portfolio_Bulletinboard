package com.company.bulletinboard.action.admin.user;

// ユーザー管理画面より、該当のユーザー編集画面へ遷移する処理
// フォームから取得したIDを基に、DBに登録されたデータを取得する
// 上記取得したデータを、ユーザー編集フォームに表示する

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * ユーザー編集用のアクション(既存データをフォームに表示)
 * @throws SQLException 
 */
public class EditUserAction extends BaseAction {
	
	// EditThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(EditUserAction.class);

	// userオブジェクトデータ領域の宣言
	private User user;

	//  取得したidを保持するため、インスタンス変数 「id 」を定義
	private int id;

	// フォームから取得したidと、インスタンス変数 「id 」との紐づけ用の、セッターメソッド
	public void setId(int id) {
		this.id = id;
	}

	// Viewのフォームから直接、userビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getUser() {
		return user;
	}

	// getUserById(id)メソッドの戻り値を、mainProcの戻り値として返している。 
	// この戻り値は、表示するビューを決定するために使用する
	@Override
	public String mainProc() throws Exception {
		// 開始ログ出力
		logger.info("EditUserAction: mainProc() start");

		getUserById(id);

		// 終了ログ出力
		logger.info("EditUserAction: mainProc() end");

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * DBに接続し、取得したIDから既存データを取得
	 * @throws SQLException 
	 */
	public String getUserById(int id) throws Exception {

		// SQLを生成
		String sql = "SELECT * FROM users WHERE user_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			//準備されたステートメントに対して、最初のパラメータとして「id」の値を設定する
			preparedStatement.setInt(1, id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装済み
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// resultSetから取得結果をUserクラスにセット
				if (resultSet.next()) {
					// userビーンオブジェクトをインスタン化する
					user = new User();
					user.setUser_id(resultSet.getInt("user_id"));
					user.setUser_name(resultSet.getString("user_name"));
					user.setPassword(resultSet.getString("password"));
					user.setAuth_type(resultSet.getInt("auth_type"));
					user.setDelete_flag(resultSet.getInt("delete_flag"));
					user.setDelete_day(resultSet.getString("delete_day"));

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
}
