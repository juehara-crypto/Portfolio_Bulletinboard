package com.company.bulletinboard.action.admin.user;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.action.admin.bulletinboard.MoveBulletinboardManagementAction;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 「ユーザー作成」画面遷移用のアクション
public class MoveCreateUserAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(MoveCreateUserAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存される。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * メイン処理
	 * ユーザー作成画面への遷移処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
	@Override
	public String mainProc() {

		// セッションの「loggedInUser」の値を、『sessionUser』に代入する
		User sessionUser = (User) session.get("loggedInUser");

		// sessionUserの値がnull(セッションにユーザー情報が存在しない)の場合に、エラーログを出力する。
		if (sessionUser == null) {
			String errorMessage = "User session is missing.";
			addActionError(errorMessage);
			logger.error("Error in MoveBulletinboardManagementAction: " + errorMessage);
			return ERROR;
		} else {
			// ログにユーザー情報を出力する
			logger.info("Session User: " + sessionUser.getUser_name());
		}

		// 成功時は "success" を返す
		return "success";

	}
}
