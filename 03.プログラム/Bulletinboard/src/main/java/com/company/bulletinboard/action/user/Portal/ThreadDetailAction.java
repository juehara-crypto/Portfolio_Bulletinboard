package com.company.bulletinboard.action.user.Portal;

// ①スレッドの詳細情報を取得する処理
//   リクエストパラメータとして渡された id を使って、スレッドの詳細情報を取得する。
//   URLパラメータからスレッドIDを取得し、それをプロパティとして保持する。
//   渡ってきたURLパラメータの変換の流れは以下の通り
//    1.id（URLパラメータ）※スレッドのリンクから送られてくる
//    2.int id（getThreadIdFromUrl()メソッド内で取得し変換）※送られて来た「id」を「int id」に変換
//    3.int threadId（mainProc()メソッド内でidを代入）※「int id」を、「int threadId」に代入
//    4.thread_id（mainProc()メソッド内でフィールドに代入）※ 「int threadI」の中にあるidを、「thread_id」に代入
// ②投稿一覧を取得する処理(「投稿一覧表示機能」用のメソッド呼び出し)
// ③その他、スレッド詳細画面の「スレッド編集/削除」ボタン表示＆非表示判定処理で使用する、スレッドの作成者IDをセッションに保存

import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.company.bulletinboard.action.user.Portal.ThreadServiceById;
import com.company.bulletinboard.dao.PostDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

public class ThreadDetailAction extends BaseAction {

	// ThreadDetailAction用のロガーのインスタンス生成
	private static final Logger logger = LogManager.getLogger(ThreadDetailAction.class);

	// URLパラメータとして渡されたスレッドIDを保持する為、フィールドとしてthread_idを定義
	private int thread_id;

	// スレッド情報を保持するフィールド
	private User thread;

	// （※投稿一覧機能結合の為、追加） User型の「postList」リストを定義
	private List<User> postList;

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// （※投稿一覧機能結合の為、追加）PostDaoクラスの「postDao」オブジェクトを生成
	PostDao postDao = new PostDao();

	// 個別のスレッド情報を取得する為のメソッド呼び出し
	private ThreadServiceById threadService = new ThreadServiceById();

	// URLパラメータとして渡されたスレッドIDを取得するメソッド 「int id」に一旦変換する
	private int getThreadIdFromUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		int id = Integer.parseInt(request.getParameter("id"));
		logger.debug("Thread ID from URL: " + id);
		return id;
	}

	// スレッド情報を取得するメソッド
	private User getThreadDetails(int threadId) {
		try {
			User thread = threadService.getThreadServiceById(threadId);
			logger.debug("Thread details: " + thread.toThreadString());
			return thread;
		} catch (Exception e) {
			logger.error("Error fetching thread details", e);
			return null;
		}
	}

	// getThreadIdFromUrl メソッドで取得したIDを、スレッド詳細画面で使用できるようにフィールドに保存するゲッター
	public int getThread_id() {
		return thread_id;
	}

	// （※投稿一覧機能結合の為、追加）スレッドID用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// スレッド情報を取得するためのゲッターメソッド
	public User getThread() {
		return thread;
	}

	// （※投稿一覧機能結合の為、追加）「postList」アクセス用のゲッターメソッド
	public List<User> getPostList() {
		return postList;
	}

	// （※投稿一覧機能結合の為、追加）「postList」アクセス用のセッターメソッド
	public void setPostList(List<User> postList) {
		this.postList = postList;
	}

	@Override
	public String mainProc() throws Exception {

		User sessionUser = (User) session.get("loggedInUser");

		// 「private int getThreadIdFromUrl()」メソッドが保持している「int id」の値を、int threadId変数に代入
		int threadId = getThreadIdFromUrl();

		// スレッド詳細画面で使用する為、URLパラメータが入っているthreadIdの値を、thread_idに代入
		this.thread_id = threadId;

		// スレッド詳細情報を取得
		thread = getThreadDetails(threadId);
		if (thread == null) {
			logger.error("Thread object is null");
			return ERROR;
		}

		// スレッドの作成者IDをセッションに保存
		// スレッド詳細画面の「スレッド編集/削除」ボタン表示＆非表示判定処理で使用する。
		// JSP側からモデルにアクセスできるようにする
		session.put("thread_user_id", thread.getUser_id());
		
	    // デバッグログでスレッドの作成者IDを確認
	    logger.debug("Thread User ID saved to session: " + session.get("thread_user_id"));

		//「投稿一覧表示機能」用のメソッド
		// postDaoオブジェクトの「getPostsByThreadId(thread_id)」メソッドを呼び出し
		// 処理結果を、「postList」リストに代入する
		postList = postDao.getPostsByThreadId(thread_id);

		// デバッグログの追加
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

		for (User post : postList) {
			// 「投稿のタイムスタンプを日本語の日付フォーマットで整形して返すメソッド」を呼び出し、結果を
			//  「formattedTimestamp」変数に代入
			String formattedTimestamp = post.getFormattedPostTimestamp();

			// 前述のメソッドのデバッグ処理
			logger.debug("Formatted timestamp for post_id " + post.getPost_id() + ": " + formattedTimestamp);
		}

		// 後続の処理(投稿編集/削除)でスレッドIDを共有する為、意図的にセッションに保存します。
		// この処理が無いとスレッドIDが後続の処理に渡れないことが判明。アクション間の連携ができない(2024/08/01追記)
		session.put("thread_id", this.thread_id);

		return SUCCESS;
	}
}
