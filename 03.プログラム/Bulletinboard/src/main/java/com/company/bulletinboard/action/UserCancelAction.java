package com.company.bulletinboard.action;

// ユーザー管理画面の「キャンセルボタン」クリック処理
// リクエストパラメータとして送信された action の値に、「usercancel」の文字列が格納されていれば、
// キャンセルボタンクリック処理成功とみなし、その文字列をstruts.xmlの「"usercancel"」アクションに返す。
// その後、ユーザーデータを取得する為、UserListActionクラスにリダイレクトする

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

import com.company.bulletinboard.interceptor.BaseAction;

public class UserCancelAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(UserCancelAction.class);

	// セッションMAPのオブジェクト
	private Map<String, Object> session;

	// セッションマップを受け取り、クラスのセッションフィールドに格納
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// actionは、アクション名を保持するためのフィールド
	private String action;

	// actionフィールドの値を返す
	public String getAction() {
		return action;
	}

	// actionフィールドに値を設定
	// リクエストパラメータとして送信された action の値(この場合はusercancelの文字列)が、
	// UserCancelAction クラス内の action フィールドに格納される
	public void setAction(String action) {
		this.action = action;
		// ここで action の値をログに出力して確認
		logger.debug("Set action parameter: " + action);
	}

	@Override
	public String mainProc() throws Exception {
		logger.debug("PostCancelAction mainProc started");

		if (request != null) {
			logger.debug("HttpServletRequest is not null");

			// setActionメッドで取得した、actionフィールドの値が「usercancel」の文字列だった場合にその文字列を返す。
			if ("usercancel".equals(action)) {
				logger.info("Cancel button clicked");
				logger.info("UserCancelAction: mainProc() end");

				return "usercancel";
			} else {
				addActionError("Invalid action parameter. Expected 'usercancel'.");

				return ERROR;
			}
		} else {
			logger.debug("HttpServletRequest is null");
			addActionError("HttpServletRequest is null.");

			return ERROR;

		}

	}
}