package com.company.bulletinboard.action.admin.user;

// ユーザー管理画面より、ユーザー削除を実効する

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

/**
 * ユーザー削除用のアクション
 * @throws SQLException 
 */
public class DeleteUserAction extends BaseAction {

	//  Userクラスをインスタンス化
	private User user = new User();

	//  取得したidを保持するため、インスタンス変数 「id 」を定義
	private int id;

	// フォームから取得したデータとidとの紐づけ用の、セッターメソッド
	public void setId(int id) {
		this.id = id;
	}

	//userビーンオブジェクトのuser_idプロパティに外部からアクセスできるようにする為の、ゲッターメソッド。
	public User getUser() {
		return user;
	}

	/**
	 * メイン処理
	 * ユーザー削除メソッド
	 * @throws SQLException 
	 */
	@Override
	public String mainProc() throws Exception {

		// SQLを生成
		String sql = "DELETE FROM users WHERE user_id = ?";

		// コネクションを取得と、ステートメントを準備
		// 処理完了後はコネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			// プレースホルダ1にidをバインド
			ps.setInt(1, id);

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
			logger.error("接続を閉じる際にエラーが発生しました: ", e);
			addActionError("No user found with the provided bulletinboard_id.");
			return ERROR;
		}

	}

}
