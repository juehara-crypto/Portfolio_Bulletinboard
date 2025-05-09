package com.company.bulletinboard.action;

// 掲示板管理画面の「キャンセルボタン」用アクションクラス
// このクラス全体の流れとして、まずリクエストオブジェクトを設定し、actionパラメータの値に基づいて条件分岐を行い、
// 適切な結果を返す。
// キャンセルボタンがクリックされたかどうかを判定し、その結果に応じて異なる処理を実行する構造になっている。

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.interceptor.BaseAction;

//ServletRequestAwareを実装することで、アクションクラス内でHTTPリクエストに関する操作を行う機能を実装できる
public class CancelAction extends BaseAction implements ServletRequestAware {

	// CancelActionクラス専用のLoggerインスタンス
	private static final Logger logger = LogManager.getLogger(CancelAction.class);

	// requestは、HTTPリクエストを保持するためのフィールド
	private HttpServletRequest request;

	// actionは、アクション名を保持するためのフィールド
	private String action;

	// actionフィールドの値を返す
	public String getAction() {
		return action;
	}

	// actionフィールドに値を設定
	public void setAction(String action) {
		this.action = action;
		// ここで action の値をログに出力して確認
		logger.debug("Set action parameter: " + action);
	}

	// ServletRequestAwareインターフェースのメソッドを実装
	// このメソッドは、Struts2がアクションに対してHttpServletRequestオブジェクトを注入するために使用される
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		logger.debug("ServletRequest received and set in CancelAction");

		// リクエストがnullで無い場合は、以下のデバッグログ出力する
		if (this.request != null) {
			logger.debug("HttpServletRequest is not null in setServletRequest");

			// デバッグログにセッションIDを出力
			logger.debug("Session ID: " + this.request.getSession().getId());

			// Enumerationを使用してリクエストパラメータをログに出力
			// request.getParameterNames()でリクエストに含まれる全てのパラメータ名を列挙するためのEnumerationを返す
			// hasMoreElements()を使って次の要素が存在するか確認し、nextElement()を使って次の要素（この場合はパラメータ名）を
			// 取得する
			logger.debug("Request parameters: ");
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				String paramName = e.nextElement();
				logger.debug(paramName + ": " + request.getParameter(paramName));
			}
		} else {
			logger.debug("HttpServletRequest is null in setServletRequest");
		}
	}

	@Override
	public String mainProc() throws Exception {
		logger.debug("CancelAction mainProc started");

		// リクエストがnullで無い場合は以下のログを出力
		if (request != null) {
			logger.debug("HttpServletRequest is not null");
			logger.debug("Request parameters: ");

			// Enumerationを使用してリクエストパラメータをログに出力
			// request.getParameterNames()でリクエストに含まれる全てのパラメータ名を列挙するためのEnumerationを返す
			// hasMoreElements()を使って次の要素が存在するか確認し、nextElement()を使って次の要素（この場合はパラメータ名）を
			// 取得する
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				String paramName = e.nextElement();
				//取得したパラメータ名を使って、その値をrequest.getParameter(paramName)で取得し、ログに出力
				logger.debug(paramName + ": " + request.getParameter(paramName));
			}

			// デバッグログにセッションIDを出力
			logger.debug("Session ID: " + request.getSession().getId());

			// アクションが文字列の"cancel"だった場合、cancelの文字列を返す
			// 結果として "list" アクションにリダイレクトされる
			if ("cancel".equals(action)) {
				logger.info("Cancel button clicked");
				return "cancel";
			} else {
				// エラーメッセージを設定し、エラー結果を返す
				addActionError("Invalid action parameter. Expected 'cancel'.");
				return ERROR; // "error" 結果を返す
			}
		} else {
			logger.debug("HttpServletRequest is null");
			// エラーメッセージを設定し、エラー結果を返す
			addActionError("HttpServletRequest is null.");
			return ERROR; // "error" 結果を返す
		}
	}

}