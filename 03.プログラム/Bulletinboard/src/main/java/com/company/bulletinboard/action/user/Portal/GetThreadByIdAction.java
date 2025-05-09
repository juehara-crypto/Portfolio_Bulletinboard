package com.company.bulletinboard.action.user.Portal;

// 利用者ポータル画面にユーザー個別のスレッド一覧を表示する
// ・処理のトリガー
//   ・「userPortal.jsp」にて「getThreadsById」のアクション指定により、インターセプター経由で、このクラスが呼び出される。
// ・メイン処理 (mainProcメソッド)
// ・ログインしているユーザーのセッション情報を取得
//   ・fetchThreadsByUserIdメソッドを使用して、ログインユーザーに関連するスレッドリストを取得
//   ・取得したスレッドリストをthreadsというリストに保存し、各スレッドに紐づく投稿件数をPostDaoクラスを使用してカウント
// ・データベースからスレッド情報を取得する処理 (fetchThreadsByUserIdメソッド)
//   ・user_idを使用してデータベースからスレッド情報を取得し、スレッドリストに追加
//   ・各スレッドのIDやタイトル、削除フラグなどが取得され、threadsリストに保存される
// ・投稿カウントの処理
//   ・PostDaoクラスを使用して、各スレッドの投稿件数を取得
//   ・取得した件数をスレッドオブジェクトに保存し、JSP側での表示に使用する
// ・セッションの管理
//   ・スレッドリストをセッションに保存することで、JSP側からアクセスできるようにする

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpSession;

import com.company.bulletinboard.dao.DatabaseConnectionManager;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.PostDao;

public class GetThreadByIdAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(GetThreadByIdAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションのユーザーIDを保持する為のフィールド
	private int user_id;
	
	// 投稿カウント用のフィールド
	private int post_count;

	// 画面表示で使用するスレッドのリスト
	private List<User> threads = new ArrayList<>();

	// セッションマップに現在のセッションを保持する為のメソッド
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// ログインユーザーIDにアクセス用のセッターメソッド
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	// ログインユーザーIDにアクセス用のゲッターメソッド
	public int getUser_id() {
		return user_id;
	}

	//JSP側からthreadsリストにアクセスする為のゲッターメソッド
	public List<User> getThreads() {
		return threads;
	}

	//JSP側からthreadsリストにアクセスする為のゲッターメソッド
	public void setThreads(List<User> threads) {
		this.threads = threads;
	}

	// 投稿カウント用処理で使用する、PostDaoクラスのインスタン生成
	private PostDao postDao = new PostDao();
	
	// 投稿カウント用フィールドのゲッター
	public int getPost_count() {
		return post_count;
	}

	// 投稿カウント用フィールドのセッター
	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

	// メイン処理
	@Override
	public String mainProc() throws Exception {
		// セッションからユーザー情報を取得する
		User sessionUser = (User) session.get("loggedInUser");

		if (sessionUser == null) {
			addActionError("User session is missing.");
			return ERROR;
		} else {
			// ログにユーザー情報を出力する
			logger.info("Session User: " + sessionUser.getUser_name());
		}

		// 画面表示で使用するユーザー個別のスレッドリストを取得
		fetchThreadsByUserId(sessionUser.getUser_id());

		if (threads == null) {
			threads = new ArrayList<>();
		}

		// スレッドリストをセッションに格納
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("threads", threads);

		// 各スレッドに対して投稿件数を取得し、それをthreadsオブジェクトに格納する。
		// これにより投稿件数が、JSPファイルでの表示が可能になる。
		for (User thread : threads) {
			int postCount = postDao.getPostCountByThreadId(thread.getThread_id());
			thread.setPost_count(postCount);
			logger.debug("Set post count for thread_id " + thread.getThread_id() + ": " + postCount);
		}

		// 処理結果を返す
		return SUCCESS;
	}

	// 利用者ポータルのユーザー個別のスレッド一覧に表示する,、スレッド情報をDBから取得する
	public void fetchThreadsByUserId(int user_id) throws Exception {
		
		// デバッグ処理開始
		logger.info("GetThreadByIdAction: fetchThreadsByUserId() start");


		// SQLを準備する
		String sql = "SELECT * FROM threads WHERE user_id = ?";
		
        try (Connection connection = DatabaseConnectionManager.getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
		
		
		// 1番目のプレースフォルダにuser_idをバインド
		preparedStatement.setInt(1, user_id);

		// クエリ結果をresultSetに代入
		try (ResultSet resultSet = preparedStatement.executeQuery()) {

		// SQLクエリが正常に実行されたことを記録している。sql変数には、実際に実行されたSQL文が格納される。
		logger.info("SQL Query Execut"	+ "ed: " + sql);
		
		// このログは、SQLクエリに渡されたパラメータ（この場合はuser_id）を記録している。
		// どのユーザーIDを使用してクエリが実行されたかを確認するために使用される。
		logger.info("Query Parameters: user_id = " + user_id);

		// リストの初期化
		threads.clear();

		// resultSetの値を、threadに代入
		while (resultSet.next()) {
			User thread = new User();

			thread.setThread_id(resultSet.getInt("thread_id"));
			thread.setThread_title(resultSet.getString("thread_title"));
			thread.setThread_delete_flag(resultSet.getInt("thread_delete_flag"));
			thread.setThread_delete_day(resultSet.getString("thread_delete_day"));

			// threadの中身をthreadsに入れる
			threads.add(thread);

			// デバッグ処理。Thread_idとThread_titleを取得
			logger.debug("Retrieved thread_id: " + thread.getThread_id());
			logger.debug("Retrieved thread_title: " + thread.getThread_title());
		}
		
        } catch (Exception e) {
            logger.error("Error fetching threads by user_id: " + user_id, e);
            throw e;
        }

		// デバッグ処理終了
		logger.info("GetThreadByIdAction: fetchThreadsByUserId() end");

		// threadsリストのサイズを確認するデバッグ処理
		logger.info("Total threads retrieved: " + threads.size());
        }

	}

	// threadsリストをJSP側に送る処理
	public List<User> getThreadsById() {
		if (threads != null && !threads.isEmpty()) {
			logger.debug("Total Threads Found: " + threads.size());
			for (User thread : threads) {
				logger.debug("Thread ID: " + thread.getThread_id());
				logger.debug("Thread Title: " + thread.getThread_title());
			}
		} else {
			logger.debug("No threads found in the list.");
		}

		return threads;

	}

}