package com.company.bulletinboard.action.admin.bulletinboard;

// 掲示板管理画面へ遷移する処理
// getAllBulletinboardsメソッドにて掲示板情報を取得し、値を「bulletinboard」オブジェクトに代入する
// 「bulletinboard」オブジェクトをJSPに返す

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.opensymphony.xwork2.ActionContext;

import java.util.Map;

import java.sql.DatabaseMetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 掲示板管理画面遷移Actionクラス
 */
public class MoveBulletinboardManagementAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(MoveBulletinboardManagementAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存される。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 画面表示で使用する掲示板のリスト
	private List<User> bulletinboards = new ArrayList<>();

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
		logger.info("MoveBulletinboardManagementAction: mainProc() start");

		// 画面表示で使用する掲示板のリストを取得
		getAllBulletinboards();

		// 終了ログ出力
		logger.info("MoveBulletinboardManagementAction: mainProc() end");

		// 処理結果を返す
		return SUCCESS;
	}

	/**
	 * 掲示板管理画面に表示する掲示板情報をDBから取得する
	 * @throws SQLException 
	 */
	public void getAllBulletinboards() throws Exception {

		// SQLを生成
		String sql = "SELECT * FROM bulletinboard";

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

			// クエリ準備
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				logger.debug("クエリ実行: " + preparedStatement.toString());

				// クエリの結果をResultSetの「resultSet」に代入する
				try (ResultSet resultSet = preparedStatement.executeQuery()) {

					// 古いデータをクリアする
					bulletinboards.clear();

					
					List<Map<String, Object>> debugData = new ArrayList<>();
					
					// フラグ変数に初回の next() の結果を格納
					boolean hasNext = resultSet.next();
					logger.debug("resultSet.next() 初回評価: " + hasNext);

					// レコードが存在する場合のみ処理を開始
					while (hasNext) {

					    // ステートメントの有効性確認
					    logger.debug("PreparedStatement: " + preparedStatement);

					    // bulletinboards リストの状態確認
					    logger.debug("bulletinboards.size(): " + bulletinboards.size());

					    // データ取得用のマップ作成とデバッグ出力
					    Map<String, Object> row = new HashMap<>();
					    row.put("bulletinboard_id", resultSet.getInt("bulletinboard_id"));
					    row.put("bulletinboard_title", resultSet.getString("bulletinboard_title"));
					    row.put("bulletinboard_content", resultSet.getString("bulletinboard_content"));
					    row.put("user_id", resultSet.getObject("user_id")); // null許容
					    debugData.add(row);
					    logger.debug("Row content: " + row);

					    // User インスタンスを生成し、各値を設定
					    User bulletinboard = new User();
					    
					    bulletinboard.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					    logger.debug("bulletinboard_id: " + bulletinboard.getBulletinboard_id());

					    bulletinboard.setBulletinboard_title(resultSet.getString("bulletinboard_title"));
					    logger.debug("bulletinboard_title: " + bulletinboard.getBulletinboard_title());

					    bulletinboard.setBulletinboard_content(resultSet.getString("bulletinboard_content"));
					    logger.debug("bulletinboard_content: " + bulletinboard.getBulletinboard_content());
					    
					    bulletinboard.setUser_id(resultSet.getInt("user_id"));
					    logger.debug("user_id: " + bulletinboard.getUser_id());
					    
					    bulletinboard.setBulletinboard_delete_flag(resultSet.getInt("bulletinboard_delete_flag"));
					    logger.debug("bulletinboard_delete_flag: " + bulletinboard.getBulletinboard_delete_flag());
					    
					    bulletinboard.setBulletinboard_delete_day(resultSet.getString("bulletinboard_delete_day"));
					    logger.debug("bulletinboard_delete_day: " + bulletinboard.getBulletinboard_delete_day());

					    // bulletinboard をリストに保存
					    bulletinboards.add(bulletinboard);

					    // 次のレコードに進む
					    // 各レコードの処理が完了した後に次のレコードの有無を確認
					    hasNext = resultSet.next();
					}


				}

				logger.debug("Resources are about to be closed: " + connection);
			} catch (SQLException e) {
				logger.error("エラー発生: ", e);
				e.printStackTrace();
				logger.error("接続を閉じる際にエラーが発生しました: ", e);
			}

		} catch (SQLException e) {
			logger.error("データベース接続中にエラーが発生しました: ", e);
			throw e;
		}

	}

	// 「bulletinboards」リストをBulletinboardManagementScreen.jspにリターンする
	public List<User> getBulletinboards() {
		return bulletinboards;
	}
}
