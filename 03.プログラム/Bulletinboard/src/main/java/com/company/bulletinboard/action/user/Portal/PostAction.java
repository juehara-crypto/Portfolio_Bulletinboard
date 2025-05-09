package com.company.bulletinboard.action.user.Portal;

// PostActionクラスの処理内容
// 1.フォームからのデータ取得：post_contentとthread_idをフォームから取得し、フィールドに保存
// 2.セッションからのユーザー情報取得：セッションからログイン中のユーザー情報を取得し、ユーザーIDを設定
// 3.デバッグログの出力：取得したデータやセッション内容をデバッグログに出力
// 4.投稿データの作成 :Userオブジェクトを作成し、投稿内容、スレッドID、ユーザーIDを設定
// 5.データベースへの保存：PostDaoクラスを使用して、投稿データをデータベースに保存

import com.company.bulletinboard.interceptor.BaseAction;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.PostDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PostAction extends BaseAction implements SessionAware {

	// ThreadServiceクラス用のデバック処理。ロガーインスタンスの生成
	private static final Logger logger = LogManager.getLogger(PostAction.class);

	private User user;

	// 投稿フォームから送られて来た、投稿内容を保持する為のフィールド
	private String post_content;

	// スレッドIDを保持する為のフィールド
	private int thread_id;

	// セッションのユーザーIDを保持する為のフィールド
	private int user_id;

	// DB登録用のPostDaoクラスのインスタンスを生成します。
	private PostDao postDao = new PostDao();

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// 投稿内容用のゲッターメソッド
	public String getPost_content() {
		return post_content;
	}

	// 投稿内容用のセッターメソッド
	public void setPost_content(String post_content) {
		this.post_content = post_content;
		if (user == null) {
			user = new User();
		}
		user.setPost_content(post_content);
		logger.debug("Post content set to: " + post_content);
	}

	// 投稿フォームから送られて来たスレッドID用のゲッターメソッド
	public int getThread_id() {
		return thread_id;
	}

	// 投稿フォームから送られて来たスレッドID用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	public int getUser_id() {
		return user_id;
	}

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存されます。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public String mainProc() throws Exception {

		// デバッグ処理の開始ログを記録
		logger.info("PostAction: mainProc() start");
		
		Integer threadId = (Integer) session.get("thread_id");

		logger.debug("Session contents: " + session);
		if (threadId != null) {
			logger.debug("Thread ID from session: " + threadId);
			this.thread_id = threadId;
		} else {
			logger.error("Thread ID not found in session");
			addActionError("Thread ID not found.");
			return ERROR;
		}
		
		logger.debug("Executing PostAction...");
		logger.debug("Post content: " + post_content);
		logger.debug("Thread ID: " + thread_id);

		if (user != null) {
			logger.debug("User post_content: " + user.getPostContent());
		}

		// セッションからユーザーオブジェクトを取得
		User loggedInUser = (User) session.get("loggedInUser");


		// セッションからユーザーIDを取得
		if (loggedInUser != null) {
			user_id = loggedInUser.getUser_id();
			logger.debug("User ID from session: " + user_id);
		} else {
			logger.debug("User ID not found in session");
			user_id = 0; // エラーハンドリング
		}

		// デバッグログ
		logger.debug("post_content: = " + post_content);

		logger.debug("thread_id: = " + thread_id);

		// セッションの内容をデバッグ出力
		// セッションがnullでなければ、セッションのすべてのエントリ（キーと値のペア）をイテレートする、
		if (session != null) {
			for (Map.Entry<String, Object> entry : session.entrySet()) {
				// 各キーと値をデバッグログに出力
				logger.debug("Session Key: " + entry.getKey() + ", Value: " + entry.getValue());
			}
		} else {
			logger.debug("Session is null");
		}



		// 「Post」モデルのインスタンスを生成
		User post = new User();

		// postオブジェクトに「postContent」の内容をセット
		post.setPost_content(post_content);
		logger.debug("post_content:  = " + post.getPost_content());

		// postオブジェクトに「thread_id」の内容をセット
		post.setThread_id(thread_id);
		logger.debug("thread_id:  = " + post.getThread_id());

		// sessionオブジェクトに入っている「user_id」をpostオブジェクトにセットする
		// セッションから現在のユーザーIDを取得し、postオブジェクトに設定します。
		// この user_idは投稿がどのユーザーによって行われたかを示すために使用されます。
		// postオブジェクトにユーザーIDをセット
		post.setUser_id(user_id);
		logger.debug("user_id:  = " + post.getUser_id());

		// 引数に「postオブジェクト」を指定して、PostDaoクラスの「save」メソッドを呼び出す
		postDao.save(post);

		// thread_idをセッションに保存 
		// PostListActionへリダイレクト処理(スレッド詳細の再取得)する際に必要な為
		session.put("thread_id", thread_id);

		// logger.debug("Post object post_content: " + user.getPostContent());

		// thread_idをクエリパラメータとしてリダイレクトURLに追加

		session.put("thread_id", this.thread_id);

		// return "success?thread_id=" + thread_id;
		return SUCCESS;
	}
}
