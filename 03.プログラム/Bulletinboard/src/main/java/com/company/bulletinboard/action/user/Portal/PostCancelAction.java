package com.company.bulletinboard.action.user.Portal;

// 「投稿編集/削除」画面のキャンセルボタンクリック処理
// 処理成功後に、後続の処理((PostListActionクラスの投稿一覧取得処理)でスレッドIDを共有する為、意図的にセッションに保存

import org.apache.struts2.interceptor.ServletRequestAware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.interceptor.BaseAction;

import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class PostCancelAction extends BaseAction implements ServletRequestAware, SessionAware {

	private static final Logger logger = LogManager.getLogger(PostCancelAction.class);

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
	// リクエストパラメータとして送信された action の値(この場合はpostcancelの文字列)が、
	// PostCancelActionクラス内の action フィールドに格納される
	public void setAction(String action) {
		this.action = action;
		// ここで action の値をログに出力して確認
		logger.debug("Set action parameter: " + action);
	}

	// スレッドIDを保持するためのフィールド
	private int thread_id;

	// thread_id用のゲッターメソッド
	public int getThread_id() {
		return thread_id;
	}

	// thread_id用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	@Override
	public String mainProc() throws Exception {
		logger.debug("PostCancelAction mainProc started");

		if (request != null) {
			logger.debug("HttpServletRequest is not null");

			// セッションからスレッドIDを取得
			Integer threadId = (Integer) session.get("thread_id");
			if (threadId != null) {
				logger.debug("Thread ID from session: " + threadId);
				this.thread_id = threadId;
			} else {
				logger.error("Thread ID not found in session");
				addActionError("Thread ID not found.");
				return ERROR;
			}

			// セッションにスレッドIDを保存
			session.put("thread_id", this.thread_id);

			// setActionメッドで取得した、actionフィールドの値が「postcancel」の文字列だった場合にその文字列を返す。
			if ("postcancel".equals(action)) {
				logger.info("Cancel button clicked");
				logger.info("PostCancelAction: mainProc() end");

				return "postcancel";
			} else {
				addActionError("Invalid action parameter. Expected 'postcancel'.");

				return ERROR;
			}
		} else {
			logger.debug("HttpServletRequest is null");
			addActionError("HttpServletRequest is null.");

			return ERROR;

		}

	}
}
