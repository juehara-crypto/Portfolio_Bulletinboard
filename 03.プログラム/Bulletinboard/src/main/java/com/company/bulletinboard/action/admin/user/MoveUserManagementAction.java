package com.company.bulletinboard.action.admin.user;

// 管理メニュー画面からユーザー管理画面へ遷移する処理
// getAllUsersメソッドにて、ユーザーデータを取得する
// 取得したユーザーデータを、「users」リストに代入しJSPに返す

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.action.admin.bulletinboard.MoveBulletinboardManagementAction;
import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * ユーザー管理画面遷移Actionクラス
 */
public class MoveUserManagementAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(MoveUserManagementAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存される。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 画面表示で使用するユーザーのリスト
	private List<User> users = new ArrayList<>();

	/**
	 * メイン処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
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
		logger.info("MoveUserManagementAction: mainProc() start");

		// 画面表示で使用するユーザーのリストを取得
		getAllUsers();

		// 終了ログ出力
		logger.info("MoveUserManagementAction: mainProc() end");

		return SUCCESS;

	}

	/**
	 * ユーザー管理画面に表示するユーザー情報をDBから取得する
	 * @throws SQLException 
	 */
	private void getAllUsers() throws Exception {

		// SQLを生成
		String sql = "SELECT * FROM users";

		// コネクション取得と、ステートメントを準備
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

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

			// 古いデータをクリアする
			users.clear();

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				List<Map<String, Object>> debugData = new ArrayList<>();

				// フラグ変数に初回の next() の結果を格納
				boolean hasNext = resultSet.next();
				logger.debug("resultSet.next() 初回評価: " + hasNext);

				// resultSetから取得結果をUserクラスにセット
				// レコードが存在する場合のみ処理を開始
				while (hasNext) {

					// ステートメントの有効性確認
					logger.debug("PreparedStatement: " + preparedStatement);

					// users リストの状態確認
					logger.debug("users.size(): " + users.size());

					// データ取得用のマップ作成とデバッグ出力
					Map<String, Object> row = new HashMap<>();
					row.put("user_id", resultSet.getInt("user_id"));
					row.put("user_name", resultSet.getString("user_name"));
					row.put("password", resultSet.getString("password"));
					row.put("auth_type", resultSet.getInt("auth_type"));
					row.put("delete_flag", resultSet.getInt("delete_flag"));
					row.put("delete_day", resultSet.getString("delete_day"));
					debugData.add(row);
					logger.debug("Row content: " + row);

					// インスタンスを生成
					User user = new User();

					user.setUser_id(resultSet.getInt("user_id"));
					logger.debug("user_id: " + user.getUser_id());

					user.setUser_name(resultSet.getString("user_name"));
					logger.debug("user_name: " + user.getUser_name());

					user.setPassword(resultSet.getString("password"));
					logger.debug("password: " + user.getPassword());

					user.setAuth_type(resultSet.getInt("auth_type"));
					logger.debug("auth_type: " + user.getAuth_type());

					user.setDelete_flag(resultSet.getInt("delete_flag"));
					logger.debug("delete_flag: " + user.getDelete_flag());

					user.setDelete_day(resultSet.getString("delete_day"));
					logger.debug("delete_day: " + user.getDelete_day());

					// 'user'オブジェクトに入っている各値を、「users」リストに保存する
					users.add(user);

					// 次のレコードに進む
					// 各レコードの処理が完了した後に次のレコードの有無を確認
					hasNext = resultSet.next();
				}

			}

			// logger.debug("Resources are about to be closed: " + connection);
			//} catch (SQLException e) {
			//	logger.error("エラー発生: ", e);
			//	e.printStackTrace();
			//	logger.error("接続を閉じる際にエラーが発生しました: ", e);
			//}

		} catch (SQLException e) {
			logger.error("データベース接続中にエラーが発生しました: ", e);
			throw e;
		}
	}

	// ゲッターメソッド
	// 取得したusersリストを返す
	public List<User> getUsers() {
		return users;
	}
}