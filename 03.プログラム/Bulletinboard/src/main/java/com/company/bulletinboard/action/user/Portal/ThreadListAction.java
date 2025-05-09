package com.company.bulletinboard.action.user.Portal;

// このクラスの役割
//  ・掲示板詳細画面にてスレッド作成処理後、このクラスにリダイレクトされ、掲示板詳細画面とそれに紐づくスレッド一覧の再取得処理を行う。
//    処理の内容は以下の通り
//    ①掲示板情報を取得
//    ②掲示板に紐づく、スレッド情報を取得
//    ③各スレッドの投稿件数を取得してthreadオブジェクトにセット。スレッド一覧に投稿件数を表示する為。
//     処理結果を、/view/BulletinboardDetailScreen.jspに渡す。

import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.company.bulletinboard.action.user.Portal.BulletinboardService;
import com.company.bulletinboard.action.user.Portal.ThreadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import com.company.bulletinboard.dao.PostDao;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public class ThreadListAction extends BaseAction {
	// ロガーのインスタンス生成
	private static final Logger logger = LogManager.getLogger(ThreadListAction.class);

	// 掲示板IDを保持する為、フィールドとしてbulletinboard_idを定義
	private int bulletinboard_id;
	
	// 掲示板ID用のセッターメソッド
	public void setBulletinboard_id(int bulletinboard_id) {
	    this.bulletinboard_id = bulletinboard_id;
	}

	// メソッドの引数用に、bulletinboardIdを定義
	private int bulletinboardId;

	// 掲示板情報を保持するフィールド
	private User bulletinboard;

	// 該当掲示板に紐づくスレッド一覧を保持するフィールド
	private List<User> threads;

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 掲示板詳細情報を取得するクラスのインスタンを生成
	private BulletinboardService bulletinboardService = new BulletinboardService();
	
	// 掲示板IDに紐づく、スレッド一覧を取得するクラスのインスタンを生成
	private ThreadService threadService = new ThreadService();

	// PostDaoクラスのインスタン生成(指定されたthread_idに関連する投稿件数を取得する処理用)
	private PostDao postDao = new PostDao();

	// 掲示板情報を取得するメソッド
	private User getBulletinboardDetails(int bulletinboardId) {
		try {
			User bulletinboard = bulletinboardService.getBulletinboardById(bulletinboardId);
			logger.debug("Bulletinboard details: " + bulletinboard.toBulletinboardString());
			return bulletinboard;
		} catch (Exception e) {
			logger.error("Error fetching bulletinboard details", e);
			return null;
		}
	}

	// 掲示板IDに紐づくスレッド情報を取得するメソッド
	private List<User> getThreadsByBulletinboardId(int bulletinboardId) {
		try {
			List<User> threads = threadService.getThreadsByBulletinboardId(bulletinboardId);
			logger.debug("Threads: " + threads);
			return threads;
		} catch (Exception e) {
			logger.error("Error fetching threads", e);
			return null;
		}
	}


	// 掲示板情報を取得するためのゲッターメソッド
	public User getBulletinboard() {
		return bulletinboard;
	}

	// スレッド一覧を取得するためのゲッターメソッド
	public List<User> getThreads() {
		return threads;
	}

	@Override
	public String mainProc() throws Exception {

		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");
		
		// デバッグ処理 掲示板IDの取得確認
		logger.debug("Bulletinboard ID: " + bulletinboard_id);

		// 「Bulletinboard ID:」が正しく設定されていることを確認
	    if (bulletinboard_id == 0) {
	        logger.error("Bulletinboard ID is not provided");
	        return ERROR;
	    }

	    // 掲示板IDが正しく設定できていれば、「bulletinboardId」に値を代入する。
	    bulletinboardId = bulletinboard_id;
	    
	    // 「 bulletinboardId」変数のデバッグ処理
	    logger.debug("BulletinboardId: " + bulletinboardId);

		// 掲示板情報を取得
		bulletinboard = getBulletinboardDetails(bulletinboardId);
		if (bulletinboard == null) {
			logger.error("Bulletinboard object is null");
			return ERROR;
		}

		// スレッド情報を取得
		threads = getThreadsByBulletinboardId(bulletinboardId);
		if (threads == null) {
			logger.error("Threads list is null");
			return ERROR;
		}

		// 各スレッドに対して投稿件数を取得し、それをスレッドオブジェクトに格納する。
		// これにより投稿件数が、JSPファイルでの表示が可能になる。
		for (User thread : threads) {
			int postCount = postDao.getPostCountByThreadId(thread.getThread_id());
			thread.setPost_count(postCount);
			logger.debug("Set post count for thread_id " + thread.getThread_id() + ": " + postCount);
		}

		return SUCCESS;
	}
}
