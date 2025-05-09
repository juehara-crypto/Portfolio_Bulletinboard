package com.company.bulletinboard.action.admin.bulletinboard;

// 掲示板管理画面より、掲示板を削除する処理

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * 掲示板削除用のアクション
 * @throws SQLException 
 */
public class DeleteBulletinboardAction extends BaseAction {

	//  Userクラスをインスタンス化
	private User bulletinboard = new User();

	//  取得したbulletinboard_id を保持するため、インスタンス変数 「bulletinboard_id」を定義
	private int bulletinboard_id;

	// フォームから取得したbulletinboard_idと、インスタンス変数のbulletinboard_idとの紐づけ用の、セッターメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	// bulletinboardのbulletinboard_idプロパティに外部からアクセスできるようにする為の、ゲッターメソッド。
	public User getBulletinboard() {
		return bulletinboard;
	}

	/**
	 * メイン処理
	 * 掲示板削除メソッド
	 * @throws SQLException 
	 */
	@Override
	public String mainProc() throws Exception {
		// デバッグ処理開始
		logger.info("DeleteBulletinboardAction: start");

		// SQLを生成
		String sql = "DELETE FROM bulletinboard WHERE bulletinboard_id = ?";

		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			// プレースホルダ1にbulletinboard_idをバインド
			ps.setInt(1, bulletinboard_id);

			// rowsAffectedをチェックし、削除が成功したかどうかを確認するロジック
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				return SUCCESS;
			} else {
				addActionError("No user found with the provided ID.");
				return ERROR;
			}

		} catch (SQLException e) {
			// ログにエラーを出力し、エラーを返す
			logger.error("SQLエラー発生: ", e);
			addActionError("An error occurred while deleting the bulletin board.");
			return ERROR;
		}
	}
}
