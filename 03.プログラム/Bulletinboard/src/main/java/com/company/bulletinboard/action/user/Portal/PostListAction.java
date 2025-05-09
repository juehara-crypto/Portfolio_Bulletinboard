package com.company.bulletinboard.action.user.Portal;

// 各クラスの処理後、リダイレクト処理として以下2つの再取得処理を行う
// ①スレッド詳細画面の再取得処理
// ② ①に紐づく投稿一覧の再取得処理

import com.company.bulletinboard.interceptor.BaseAction;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.PostDao;
import com.company.bulletinboard.action.user.Portal.ThreadServiceById;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;

public class PostListAction extends BaseAction implements SessionAware {

	private static final Logger logger = LogManager.getLogger(PostListAction.class);
	
	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;
	
	// スレッドIDを保持する為のクラスフィールド
	private int thread_id;
	
	// スレッド情報を保持するフィールド
	private User thread;
	
	//（投稿一覧取得用） User型の「postList」リストを定義	
	private List<User> postList;
	
	// 個別のスレッド情報を取得する為、ThreadServiceByIdクラスのthreadServiceインスタンスを生成
	private ThreadServiceById threadService = new ThreadServiceById();
	
	//（投稿一覧取得用）PostDaoクラスの「postDao」オブジェクトを生成
	private PostDao postDao = new PostDao();

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// thread_id用のゲッターメソッド
	public int getThread_id() {
		return thread_id;
	}

	// thread_id用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// スレッド情報を取得する為のゲッターメソッド
	public User getThread() {
		return thread;
	}
  
	//（投稿一覧取得用） User型の「postList」リストを定義
	public List<User> getPostList() {
		return postList;
	}

	//（投稿一覧取得用）「postList」アクセス用のセッターメソッド
	public void setPostList(List<User> postList) {
		this.postList = postList;
	}

	@Override
	public String mainProc() throws Exception {

		// デバック処理開始
		logger.info("PostListAction: mainProc() start");

		// セッションに格納されているthread_idの値をInteger型の「threadId」変数に代入
		Integer threadId = (Integer) session.get("thread_id");

		// threadIdの値が空で無ければ、threadIdの値を、クラスフィールドの「thread_id」に代入する
		// クラス内でセッションから送られて来た、スレッドIDが使用できるようにする。
		logger.debug("Session contents: " + session);
		if (threadId != null) {
			logger.debug("Thread ID from session: " + threadId);
			this.thread_id = threadId;
		} else {
			logger.error("Thread ID not found in session");
			addActionError("Thread ID not found.");
			return ERROR;
		}

		// スレッド情報取得処理結果を、モデルに保存
		// モデルにデータが無ければエラー処理
		try {
			thread = getThreadDetails(thread_id);
			if (thread == null) {
				return ERROR;
			}
		} catch (Exception e) {
			logger.error("Error in mainProc", e);
			return ERROR;
		}

		// 投稿データ取得処理後、postListに保存
		postList = postDao.getPostsByThreadId(thread_id);

		// デバック処理。postListの中身をUserモデルに代入し、その中のデータを表示する処理。
		if (postList != null) {
			for (User post : postList) {
				logger.debug("post_id = " + post.getPost_id());
				logger.debug("user_name = " + post.getUser_name());
				logger.debug("post_timestamp = " + post.getPost_timestamp());
				logger.debug("post_content = " + post.getPost_content());
			}
		} else {
			logger.debug("postList is null");
		}

		//（投稿一覧取得用）
		for (User post : postList) {
			// 「投稿のタイムスタンプを日本語の日付フォーマットで整形して返すメソッド」を呼び出し、結果を
			//  「formattedTimestamp」変数に代入
			String formattedTimestamp = post.getFormattedPostTimestamp();
			logger.debug("Formatted timestamp for post_id " + post.getPost_id() + ": " + formattedTimestamp);
		}
		return SUCCESS;
	}

	// スレッドIDに紐づく、スレッド情報取得。処理結果をモデルに保存する
	private User getThreadDetails(int thread_id) {
		try {
			User thread = threadService.getThreadServiceById(thread_id);
			logger.debug("Thread details: " + thread.toThreadString());
			return thread;
		} catch (Exception e) {
			logger.error("Error fetching thread details", e);
			return null;
		}
	}
}
