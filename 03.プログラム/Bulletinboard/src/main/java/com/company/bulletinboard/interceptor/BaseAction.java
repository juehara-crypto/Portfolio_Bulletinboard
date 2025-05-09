package com.company.bulletinboard.interceptor;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;

/**
 * BaseAction
 * 各ActionクラスのBaseクラス
 */

//ServletRequestAwareを実装することで、アクションクラス内でHTTPリクエストに関する操作を行う機能を実装できる 
public abstract class BaseAction extends ActionSupport implements ServletRequestAware, SessionAware {

	/**
	 * Loggerインスタンスをクラスレベルで保持するためのstaticな定義。
	 * BaseActionクラス全体で利用するLoggerインスタンスを取得する。
	 * LogManager.getLoggerメソッドの引数としてBaseAction.classを指定することで、
	 * BaseActionクラス自体に関連付けられたLoggerを取得する。
	 */
	protected static final Logger logger = LogManager.getLogger(BaseAction.class);

	// BaseActionクラス内でHTTPリクエストオブジェクトを保持するためのフィールド
	protected HttpServletRequest request;

	// SessionAwareを実装することで使用可能になる
	protected Map<String, Object> session;

	@Override
	// ServletRequestAwareを実装した場合、Struts2フレームワークはsetServletRequestメソッドを通じてHTTPリクエストをアクションに
	// 注入する。このメソッドでHttpServletRequestオブジェクトを受け取り、アクション内でそのリクエストに関する操作を
	// 行うことができる 
	public void setSession(Map<String, Object> session) {
		this.session = session;
		logger.debug("BaseAction: ServletRequest set");
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	static {
		// ログディレクトリの作成
		File logDir = new File("logs");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	}

	/**
	 * executeメソッド
	 * @return 処理結果の文字列
	 */
	public String execute() {

		/** 処理結果 */
		String result = "error";

		// 開始ログ出力
		logger.info("処理開始：" + this.getClass().getName());

		try {
			// メイン処理(各クラスにて実装)
			result = mainProc();

		} catch (Exception e) {
			// 例外の詳細をログに出力
			logger.error("エラーが発生しました", e);
		}

		// 終了ログ出力
		logger.info("処理終了：" + this.getClass().getName());
		logger.debug("Result: " + result);

		return result;
	}

	/**
	 * 継承先のクラスで実装されるメイン処理
	 * @return 処理結果の文字列
	 */
	abstract public String mainProc() throws Exception;
}
