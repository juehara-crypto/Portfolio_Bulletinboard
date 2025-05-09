package com.company.bulletinboard.action.login;

import com.company.bulletinboard.interceptor.BaseAction;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogoutAction extends BaseAction {

	//  BaseActionクラスの「mainProc()」メソッドをオーバーライド
	@Override
	public String mainProc() throws Exception {
		
		// LogoutActionクラス専用のLoggerインスタンス
		final Logger logger = LogManager.getLogger(LogoutAction.class);

		// 現在のセッションを取得
		HttpSession session = ServletActionContext.getRequest().getSession(false);

		if (session != null) {
			// セッションを無効化
			session.invalidate();
			logger.debug("セッションが無効化されました。セッションID: " + session.getId());
		}

		// ログインページにリダイレクト
		logger.info("ログアウト処理が成功しました。ログインページにリダイレクトします。");
		return "success";

	}
}
