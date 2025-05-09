package com.company.bulletinboard.action.user.Portal;

// ①掲示板情報を取得
// ②スレッド情報を取得
// ③各スレッドの投稿件数を取得してthreadオブジェクトにセット。スレッド一覧に投稿件数を表示する

import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.company.bulletinboard.action.user.Portal.BulletinboardService;
import com.company.bulletinboard.action.user.Portal.ThreadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import com.company.bulletinboard.dao.PostDao;

public class BoardDetailAction extends BaseAction {
	// ロガーのインスタンス生成
	private static final Logger logger = LogManager.getLogger(BoardDetailAction.class);

	// URLパラメータとして渡された掲示板IDを保持する為、フィールドとしてbulletinboard_idを定義
	private int bulletinboard_id;

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

	// サービスクラスのインスタンス
	private BulletinboardService bulletinboardService = new BulletinboardService();
	private ThreadService threadService = new ThreadService();

	// PostDaoクラスのインスタン生成
	private PostDao postDao = new PostDao();

	// URLパラメータとして渡された掲示板IDを取得するメソッド 「int id」に一旦変換する
	private int getBulletinboardIdFromUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		int id = Integer.parseInt(request.getParameter("id"));
		logger.debug("Bulletinboard ID from URL: " + id);
		return id;
	}

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

	// getBulletinboardIdFromUrl メソッドで取得したIDを、掲示板詳細画面で使用できるようにフィールドに保存するゲッター
	public int getBulletinboard_id() {
		return bulletinboard_id;
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

		// 「getBulletinboardIdFromUrl()」メソッドが保持している「int id」の値を、int bulletinboardId変数に代入
		int bulletinboardId = getBulletinboardIdFromUrl();

		// 掲示板詳細画面で使用する為、URLパラメータが入っているbulletinboardIdの値を、bulletinboard_idに代入
		this.bulletinboard_id = bulletinboardId;

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
