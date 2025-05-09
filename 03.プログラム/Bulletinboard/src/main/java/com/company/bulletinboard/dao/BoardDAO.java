package com.company.bulletinboard.dao;

// 掲示板管理画面の「キャンセルボタン」処理時に呼び出されるメソッド。
// ListActionクラスから呼び出されて、DBから全ての掲示板データを取得し「boards」リストに保存する。
// 「boards」リストを、ListActionクラスに返す。

import com.company.bulletinboard.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BoardDAO {

	// ロガーの設定
	private static final Logger logger = LogManager.getLogger(BoardDAO.class);

	//  データベースから取得したデータを「boards」リストに保存する処理
	public List<User> getAllBoards() {

		// 「boards」アレイリストを定義
		List<User> boards = new ArrayList<>();

		// コネクションとステートメントを取得
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		try (Connection connection = DatabaseConnectionManager.getConnection();
				// ステートメントとSQLの準備
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bulletinboard")) {

			// クエリ結果を「rs」ResultSetに保存
			try (ResultSet rs = preparedStatement.executeQuery()) {

				// 「rs」ResultSetに入っているデータをboardオブジェクトの各プロパティにセット
				while (rs.next()) {

					// Userモデルクラスのboardオブジェクトを生成
					User board = new User();

					// boardオブジェクトに「rs」の値をセット
					board.setBulletinboard_id(rs.getInt("bulletinboard_id"));
					board.setBulletinboard_title(rs.getString("bulletinboard_title"));
					board.setBulletinboard_content(rs.getString("bulletinboard_content"));
					board.setBulletinboard_delete_flag(rs.getInt("bulletinboard_delete_flag"));
					board.setBulletinboard_delete_day(rs.getString("bulletinboard_delete_day"));

					// boardオブジェクトの値を、boardsリストに入れる
					boards.add(board);
				}
				logger.debug("Number of bulletin boards retrieved: " + boards.size());
			} catch (Exception e) {
				logger.error("Error in getAllBoards", e);
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// SQLExceptionの処理
			logger.error("Error obtaining database connection", e);
			e.printStackTrace();
		}

		// 全てのUserオブジェクトを含むboardsリストを返す
		return boards;
	}
}