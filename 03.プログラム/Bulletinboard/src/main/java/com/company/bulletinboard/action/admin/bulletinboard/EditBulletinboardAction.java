package com.company.bulletinboard.action.admin.bulletinboard;

// 掲示板管理画面より、該当の掲示板編集画面へ遷移する処理
// フォームから取得した掲示板IDを基に、DBに登録されたデータを取得する
// 上記取得したデータを、掲示板編集フォームに表示する

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

import com.company.bulletinboard.action.user.Portal.EditThreadAction;
import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * 掲示板編集用のアクション(既存データをフォームに表示)
 * @throws SQLException 
 */
public class EditBulletinboardAction extends BaseAction {

	// EditThreadAction用のロガーインスタンスを生成
	private static final Logger logger = LogManager.getLogger(EditBulletinboardAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存される。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// bulletinboardオブジェクトのデータ領域を宣言
	private User bulletinboard;

	//  取得したbulletinboard_idを保持するため、インスタンス変数 「bulletinboard_id 」を定義
	private int bulletinboard_id;

	// フォームから取得したbulletinboard_idと、インスタンス変数「bulletinboard_id 」との紐づけ用の、セッターメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	// Viewやその他の処理から個別にbulletinboard_idにアクセスさせる為のメソッド
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// Viewのフォームから直接、bulletinboardビーンオブジェクトの各プロパティにアクセスする為のゲッターメソッド
	public User getBulletinboard() {
		return bulletinboard;
	}

	// getBulletinboardById()メソッドの戻り値を、mainProcの戻り値として返している。 
	// この戻り値は、表示するビューを決定するために使用する
	@Override
	public String mainProc() throws Exception {

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

		// 開始ログ出力
		logger.info("EditBulletinboardAction: mainProc() start");

		getBulletinboardById(bulletinboard_id);

		// 終了ログ出力
		logger.info("EditBulletinboardAction: mainProc() end");

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * DBに接続し、取得したIDから既存データを取得
	 * @throws SQLException 
	 */
	public String getBulletinboardById(int bulletinboard_id) throws Exception {

		// SQLを生成
		String sql = "SELECT * FROM bulletinboard WHERE bulletinboard_id = ?";

		// コネクションを取得と、ステートメントを準備
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
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

			PreparedStatement preparedStatement = connection.prepareStatement(sql);

			//準備されたステートメントに対して、最初のパラメータとして「bulletinboard_id」の値を設定する
			preparedStatement.setInt(1, bulletinboard_id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装済み
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// resultSetから取得結果をUserクラスにセット
				if (resultSet.next()) {
					// bulletinboardビーンオブジェクトをインスタン化する
					bulletinboard = new User();

					// ResultSetに入っている各値をbulletinboardオブジェクトに入れる
					bulletinboard.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					logger.debug("bulletinboard_id: = " + bulletinboard.getBulletinboard_id());

					bulletinboard.setBulletinboard_title(resultSet.getString("bulletinboard_title"));
					logger.debug("bulletinboard_title: = " + bulletinboard.getBulletinboard_title());

					bulletinboard.setBulletinboard_content(resultSet.getString("bulletinboard_content"));
					logger.debug("bulletinboard_content: = " + bulletinboard.getBulletinboard_content());

					bulletinboard.setBulletinboard_delete_flag(resultSet.getInt("bulletinboard_delete_flag"));
					logger.debug("bulletinboard_delete_flag: = " + bulletinboard.getBulletinboard_delete_flag());

					bulletinboard.setBulletinboard_delete_day(resultSet.getString("bulletinboard_delete_day"));
					logger.debug("bulletinboard_delete_day: = " + bulletinboard.getBulletinboard_delete_day());

					return SUCCESS;

				} else {
					// 取得結果が存在しない場合にERRORを返す
					addActionError("No bulletinboard found with the provided bulletinboard_id.");
					return ERROR;
				}

			} catch (SQLException e) {
				// ログにエラーを出力し、エラーを返す
				logger.error("接続を閉じる際にエラーが発生しました: ", e);
				addActionError("No user found with the provided bulletinboard_id.");
				return ERROR;
			}

		} catch (SQLException e) {
			logger.error("データベース接続中にエラーが発生しました: ", e);
			throw e;
		}
	}
}
