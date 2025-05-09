package com.company.bulletinboard.action.user.Portal;

// 画面表示で使用するユーザー個別の投稿リストを取得し、リストをJSPに返す処理。

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class GetPostByIdAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(GetPostByIdAction.class);

	// セッションMAPのオブジェクト「session」を生成
	private Map<String, Object> session;

	// セッションのユーザーIDを保持する為のフィールド
	private int user_id;

	// 画面表示で使用する投稿のリスト
	private List<User> posts = new ArrayList<>();

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

	//JSP側からpostsリストにアクセスする為のゲッターメソッド
	public List<User> getPosts() {
		return posts;
	}

	//JSP側からpostsリストにアクセスする為のセッターメソッド
	public void setPosts(List<User> posts) {
		this.posts = posts;
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

		// 画面表示で使用するユーザー個別の投稿リストを取得
		fetchPostsByUserId(sessionUser.getUser_id());

		if (posts == null) {
			posts = new ArrayList<>();
		}

		// 投稿リストをセッションに格納
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("posts", posts);

		// 処理結果を返す
		return SUCCESS;
	}

	// 利用者ポータルの投稿一覧にユーザー個別の投稿を表示する投稿情報をDBから取得する
	public void fetchPostsByUserId(int user_id) throws Exception {

		// デバッグ処理開始
		logger.info("GetPostByIdAction: fetchPostsByUserId() start");

		// SQLを準備する
		String sql = "SELECT posts.*, threads.thread_id \n"
				+ "FROM posts \n"
				+ "JOIN threads ON posts.thread_id = threads.thread_id \n"
				+ "WHERE (posts.post_delete_flag IS NULL OR posts.post_delete_flag = 0) \n"
				+ "AND posts.user_id = ?;\n"
				+ "";

		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			// 1番目のプレースフォルダにuser_idをバインド
			preparedStatement.setInt(1, user_id);

			// クエリ結果をresultSetに保存
			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				// SQLクエリが正常に実行されたことを記録する、sql変数には、実際に実行されたSQL文が格納されている。
				logger.info("SQL Query Execut" + "ed: " + sql);

				// SQLクエリに渡されたパラメータ（この場合はuser_id）を記録する。
				// どのユーザーIDを使用してクエリが実行されたかを確認するために使用される。
				logger.info("Query Parameters: user_id = " + user_id);

				// リストの初期化
				posts.clear();

				boolean hasPosts = false;

				// resultSetの値を、postに代入
				while (resultSet.next()) {
					User post = new User();

					post.setPost_id(resultSet.getInt("post_id"));
					post.setPost_content(resultSet.getString("post_content"));
					post.setPost_delete_flag(resultSet.getInt("post_delete_flag"));
					post.setPost_delete_day(resultSet.getString("post_delete_day"));
					post.setUser_id(resultSet.getInt("user_id"));
					post.setThread_id(resultSet.getInt("thread_id"));

					// postの中身をpostsに入れる
					posts.add(post);

					// デバッグ処理。post_idとpost_contentを取得
					logger.debug("Retrieved post_id: " + post.getPost_id());
					logger.debug("Retrieved post_content: " + post.getPost_content());
					// 取得したユーザーID
					logger.debug("User ID: " + post.getUser_id());

					hasPosts = true; // データが存在することを示す
				}

				// すべてのresultSet処理が終わった後で、フラグに基づいてログを出力
				if (hasPosts) {
					for (User post : posts) {
						logger.info(
								"Post found: post_id = " + post.getPost_id() + ", thread_id = " + post.getThread_id());
					}
				} else {
					logger.info("No posts found for user_id = " + user_id);
				}

				// デバッグ処理終了
				logger.info("GetPostByIdAction: fetchPostsByUserId() end");

				// postsリストのサイズを確認するデバッグ処理
				logger.info("Total posts retrieved: " + posts.size());

			}

		} catch (SQLException e) {
			logger.error("Error closing connection", e);
		}
	}

	// postsリストをJSP側に送る処理
	public List<User> getPostsById() {
		if (posts != null && !posts.isEmpty()) {
			logger.debug("Total Posts Found: " + posts.size());
			for (User post : posts) {
				logger.debug("Post ID: " + post.getPost_id());
				logger.debug("Post Content: " + post.getPost_content());
			}
		} else {
			logger.debug("No posts found in the list.");
		}

		return posts;

	}

}
