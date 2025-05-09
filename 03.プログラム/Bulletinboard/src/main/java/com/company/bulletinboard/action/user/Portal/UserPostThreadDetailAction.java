package com.company.bulletinboard.action.user.Portal;

// ①ユーザー個別の投稿に紐づく、親スレッドの詳細情報を取得する処理
//   リクエストパラメータとして渡された投稿の「postId」 を使って、親スレッドの詳細情報を取得する。
//   まず、URLパラメータとして渡された「postId」に入っている「投稿ID」を取得し、『postIdParam』に値を保存する。
//   『postIdParam』に入っている投稿IDをクラスフィールドの「postId」に代入する
//    postIDに入っている投稿IDを使って、スレッドIDを取得する。  
//   取得したスレッドIDを使って、スレッド詳細情報を取得する
//   処理の流れは以下の通り
//    1.postId（URLパラメータ）※ユーザー個別の投稿リンクから送られてくる
//    2.送られて来たpostIdを「getPostIdFromUrl()」を使って、「postIdParam」変数に一旦保管する
//      postIdParamがnullまたは空文字で無ければ、postIdParamの値を整数に変換し、「postId」に保存する。
//    3.「getThreadIdFromPostId(int postId)」メソッドを使って、取得した投稿IDを基にスレッドIDを取得する。「threadId」変数に保管する。
//    4.「getThreadDetails(int threadId)」にて、投稿IDから取得したスレッドIDを引数にし、スレッド詳細情報を取得する。「thread」オブジェクトに保管する。
// ② ①で取得した、「親スレッドに紐づく、投稿を取得する」処理
// ③その他、スレッド詳細画面の「スレッド編集/削除」ボタン表示＆非表示判定処理で使用する、スレッドの作成者IDをセッションに保存

import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.company.bulletinboard.action.user.Portal.UserPostThreadService;
import com.company.bulletinboard.dao.UserPostDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

public class UserPostThreadDetailAction extends BaseAction {

	// UserPostThreadDetailAction用のロガーのインスタンス生成
	private static final Logger logger = LogManager.getLogger(UserPostThreadDetailAction.class);

	// フィールドとしてthread_idを定義
	private int thread_id;

	// スレッド情報を保持するフィールド
	private User thread;

	// （親スレッドに紐づく、投稿取得用として定義） User型の「postList」リストを定義
	private List<User> postList;

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションマップに現在のセッションを保持する為のメソッド。このクラスの「session」オブジェクトに
	// セッションが保存される。
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// （親スレッドに紐づく、投稿取得処用として定義） UserPostDaoクラスの「userpostDao」オブジェクトを生成
	UserPostDao userpostDao = new UserPostDao();

	// 親スレッドの情報を取得する為のクラスオブジェクトを生成
	private UserPostThreadService userthreadService = new UserPostThreadService();

	// URLパラメータとして渡された投稿IDを取得するメソッド
	private int getPostIdFromUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();

		// 投稿IDを取得
		String postIdParam = request.getParameter("postId");

		// postIdParamがnullまたは空文字の場合のチェック: パラメータが存在しない場合にエラーをログに記録し、例外をスローする
		if (postIdParam == null || postIdParam.isEmpty()) {
			logger.error("Post ID parameter is missing or empty.");
			throw new IllegalArgumentException("Post ID parameter is required.");
		}

		int postId;
		try {
			// postIdParamがnullまたは空文字では無い場合は、postIdParamの投稿IDの値を整数に変換し、「postId」に保存する。
			postId = Integer.parseInt(postIdParam);

			//NumberFormatExceptionのキャッチ: postIdParamが整数に変換できない場合にエラーメッセージをログに記録し、適切な例外をスローする
		} catch (NumberFormatException e) {
			logger.error("Invalid Post ID format: " + postIdParam, e);
			throw new IllegalArgumentException("Invalid Post ID format.");
		}

		logger.debug("Post ID from URL: " + postId);

		// postIdを返す
		return postId;
	}

	// 投稿IDから、親スレッドのスレッドIDを取得するメソッド
	private int getThreadIdFromPostId(int postId) throws SQLException {
		int threadId = userpostDao.getThreadIdByPostId(postId);
		logger.debug("Thread ID retrieved by Post ID: " + threadId);
		return threadId;
	}

	// 親スレッドの情報を取得するメソッド
	private User getThreadDetails(int threadId) {
		try {
			// サービスロジック(UserPostThreadService)からスレッド情報を取得
			User thread = userthreadService.getThreadServiceById(threadId);
			logger.debug("Thread details: " + thread.toThreadString());
			return thread;
		} catch (SQLException e) {
			// データベース関連のエラーが発生した場合、エラーメッセージを設定
			addActionError("Error occurred while retrieving thread details.");
			logger.error("SQL Error fetching thread details", e);
			return null;
		} catch (Exception e) {
			// その他の例外が発生した場合、エラーメッセージを設定
			addActionError("An unexpected error occurred.");
			logger.error("Unexpected error fetching thread details", e);
			return null;
		}
	}

	// 親スレッドの、スレッドID用のセッターメソッド
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// 親スレッド情報を取得するためのゲッターメソッド
	public User getThread() {
		return thread;
	}

	// 親スレッドに紐づく投稿用「postList」へアクセス用のゲッターメソッド
	public List<User> getPostList() {
		return postList;
	}

	// 親スレッドに紐づく投稿用「postList」へアクセス用のセッターメソッド
	public void setPostList(List<User> postList) {
		this.postList = postList;
	}

	@Override
	public String mainProc() throws Exception {

		User sessionUser = (User) session.get("loggedInUser");

		// URLから投稿IDを取得
		int postId = getPostIdFromUrl();

		// 投稿IDから親スレッドのIDを取得
		int threadId = getThreadIdFromPostId(postId);

		// 親スレッドのスレッドIDをフィールドに保存
		this.thread_id = threadId;

		// 親スレッド情報を取得
		this.thread = getThreadDetails(threadId);

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

		// 親スレッドに紐づく投稿を取得する為のメソッド呼び出し。
		// UserPostDaoオブジェクトの「getPostsByThreadId(thread_id)」メソッドを呼び出す。
		// 処理結果を、「postList」リストに代入する
		postList = userpostDao.getPostsByThreadId(thread_id);

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