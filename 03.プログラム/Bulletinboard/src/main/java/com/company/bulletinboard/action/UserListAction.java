
package com.company.bulletinboard.action;

//  ユーザー管理画面の「キャンセルボタン」クリック処理
// UserCancelActionクラスのリダイレクト先アクションクラス
// 「キャンセルボタン」クリック処理時に、UserListDAOクラスのgetAllUsersを呼び出して、全てのユーザーデータを取得する
// データ取得後、usersリストに格納しJSPに返す

import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.UserListDAO;
import com.company.bulletinboard.interceptor.BaseAction;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserListAction extends BaseAction {

	//  UserListActionクラス専用のLoggerインスタンス
	private static final Logger logger = LogManager.getLogger(UserListAction.class);

	// Userモデルクラスのusersリストの定義
	private List<User> users;

	// BaseActionクラスの「mainProc()」メソッドをオーバーライド
	@Override
	public String mainProc() throws Exception {
		logger.debug("UserListAction mainProc started");

		try {
			logger.debug("Creating UserListDAO instance");

			// UserListDAOクラスのインタンスを生成
			UserListDAO userdao = new UserListDAO();
			logger.debug("Fetching all users from database");

			// getAllUsersメソッドを呼び出して、データベースからユーザーの全レコードを取得し、
			// それをusersリストに格納
			users = userdao.getAllUsers();

		    logger.debug("Number of users retrieved: " + users.size());

			// usersリスト内の各Userオブジェクトに対して、toStringメソッドを呼び出して
			// ログにその内容を出力する。これにより、各ユーザーの詳細情報がログに記録される。
			for (User user : users) {
			logger.debug("User: " + user.toUserString());
			}
			logger.debug("UserListAction mainProc finished");
		} catch (Exception e) {
			logger.error("Error in UserListAction mainProc", e);
			throw e;
		}
		System.out.println("UserListAction execute started");
		// 処理成功時に「SUCCESS」を返す
		return SUCCESS;
	}

	/**
	 * ④usersフィールドの値を返すゲッターメソッド。
	 * 
	 * @return usersフィールドの値
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * usersフィールドの値を設定するセッターメソッド。
	 * 
	 * @param users 新しいusersフィールドの値
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

}
