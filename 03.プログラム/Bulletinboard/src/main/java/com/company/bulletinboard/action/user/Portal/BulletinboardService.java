package com.company.bulletinboard.action.user.Portal;

// 掲示板詳細画面へ表示する、掲示板情報を取得する処理
// BoardDetailActionクラスから、このクラスのgetBulletinboardByIdメソッドが呼び出される
// 掲示板情報を取得後、値をbulletinboardオブジェクトに入れて、呼び出し元に返す

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

public class BulletinboardService extends BaseAction {

	// BulletinboardServiceのロガーインスタンス生成
	private static final Logger logger = LogManager.getLogger(BulletinboardService.class);

	// 掲示板のIDを保持するフィールド
	private int bulletinboard_id;

	// 掲示板IDを設定するためのメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	@Override
	public String mainProc() throws Exception {

		User bulletinboard = getBulletinboardById(bulletinboard_id);
		if (bulletinboard == null) {
			return ERROR;
		}

		// 結果をリクエストスコープに保存
		request.setAttribute("bulletinboard", bulletinboard);

		return SUCCESS;
	}

	// IDに基づいて掲示板情報を取得するメソッド。データベース接続を確立し、クエリを実行して結果を User オブジェクトに設定
	public User getBulletinboardById(int id) throws Exception {
		logger.info("BulletinboardService: mainProc() start");

		// bulletinboardオブジェクトの初期化
		User bulletinboard = null;

		// SQLの準備
		String query = "SELECT * FROM bulletinboard WHERE bulletinboard_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			// デバックログ(DB接続開始)
			logger.info("Database connection opened.");

			// ステートメントの一番目の引数を「id」に指定する
			preparedStatement.setInt(1, id);

			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装済み
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// resultSetの各値を、bulletinboardオブジェクトに代入する
				while (resultSet.next()) {
					bulletinboard = new User();
					bulletinboard.setBulletinboard_id(resultSet.getInt("bulletinboard_id"));
					bulletinboard.setBulletinboard_title(resultSet.getString("bulletinboard_title"));
					bulletinboard.setBulletinboard_content(resultSet.getString("bulletinboard_content"));

					// デバッグログ
					logger.debug("bulletinboard_id: = " + bulletinboard.getBulletinboard_id());
					logger.debug("bulletinboard_title: = " + bulletinboard.getBulletinboard_title());
					logger.debug("bulletinboard_content: ID = " + bulletinboard.getBulletinboard_content());

					// デバックログを標準出力						
					// System.out.println("Bulletinboard_id: " + bulletinboard.getBulletinboard_id());
					// System.out.println("Bulletinboard_title: = " + bulletinboard.getBulletinboard_title());
					// System.out.println("bulletinboard_content: = " + bulletinboard.getBulletinboard_content());

				}
			} catch (SQLException e) {
				// ログにエラーを出力し、エラーを返す
				logger.error("接続を閉じる際にエラーが発生しました: ", e);
			}
			// デバックログ(メソッド処理終了)
			logger.info("BulletinboardService: getBulletinboardById() end");
		}
		// bulletinboardオブジェクトを返す
		return bulletinboard;
	}

}
