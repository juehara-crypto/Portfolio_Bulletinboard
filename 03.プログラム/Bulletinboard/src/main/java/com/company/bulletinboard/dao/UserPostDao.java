package com.company.bulletinboard.dao;

// UserPostDaoクラスの処理内容
// ユーザー個別の投稿に紐づく、親スレッドの投稿データを取得する処理。
// UserPostThreadDetailActionクラスから、getPostsByThreadId(int thread_id)メソッド呼び出される。
// 親スレッドに紐づく投稿を取得し、結果を返す。


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.bulletinboard.model.User;
import com.company.bulletinboard.action.user.Portal.PostAction;
import com.company.bulletinboard.dao.DatabaseConnectionManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

public class UserPostDao {

	// UserPostDao用のデバック処理。ロガーインスタンスの生成
	private static final Logger logger = LogManager.getLogger(UserPostDao.class);

	// フィールドとしてthread_idを定義
	private int thread_id;

	// スレッドID用のセッターを定義
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	// スレッドID用のゲッターを定義
	public int getThread_id() {
		return thread_id;
	}

	
	/**
	 * 指定されたスレッドIDに関連する投稿のリストを取得します。
	 * 投稿には、投稿内容(post_content)、投稿日時(post_timestamp)、
	 * 投稿者のユーザーID(user_id)およびユーザー名(user_name)が含まれます。
	 * 結果は投稿日時の昇順でソートされ、古い投稿から新しい投稿へと並びます。
	 * 
	 * @param thread_id 取得したい投稿が属するスレッドのID
	 * @return 指定されたスレッドに関連する投稿のリスト
	 * @throws SQLException データベースアクセスエラーが発生した場合
	 */
	
	public List<User> getPostsByThreadId(int thread_id) {

		// メソッドの最初にスレッドIDをデバッグログとして出力
		logger.debug("Received thread_id: " + thread_id);
		System.out.println("Received thread_id: " + thread_id);

		// 投稿データ用のUser型アレイリストを定義
		List<User> postList = new ArrayList<>();

		// JDBCを使用してデータベース接続を設定
		try (Connection connection = DatabaseConnectionManager.getConnection()) {

			// SQLクエリ: 指定されたスレッドIDに関連するすべての投稿とその投稿者の名前を取得し、投稿日時の昇順で並べ替えます。
			String query = "SELECT posts.post_id, posts.thread_id, posts.user_id, posts.post_content, posts.post_timestamp, users.user_name "
			    + "FROM posts "  // postsテーブルから投稿情報を取得します。
			    + "JOIN users ON posts.user_id = users.user_id "  // postsテーブルとusersテーブルをuser_idで結合し、投稿者の名前を取得します。
			    + "JOIN threads ON posts.thread_id = threads.thread_id "  // postsテーブルとthreadsテーブルをthread_idで結合します。ここでの結合は、スレッドIDが有効であることを確認する役割も果たします。
			    + "WHERE posts.thread_id = ? "  // 指定されたthread_idに関連する投稿のみを対象とします。クエリ実行時にプレースホルダ（?）に値がセットされます。
			    + "ORDER BY posts.post_timestamp ASC";  // 投稿日時(post_timestamp)を昇順で並べ替え、古い投稿から順に表示します。


			// クエリを実行する直前にログ出力
			logger.debug("Executing query: " + query + " with thread_id: " + thread_id);

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, thread_id);

				// クエリ結果をresultSetに保存
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {

						// User型のpostオブジェクトを生成
						User post = new User();

						// postオブジェクトにクエリ結果を保存
						post.setThread_id(resultSet.getInt("thread_id"));
						post.setUser_id(resultSet.getInt("user_id"));
						post.setPost_id(resultSet.getInt("post_id"));
						post.setUser_name(resultSet.getString("user_name"));
						post.setPost_timestamp(resultSet.getTimestamp("post_timestamp"));
						post.setPost_content(resultSet.getString("post_content"));
						postList.add(post);

						// デバッグ処理
						logger.debug("thread_id = " + post.getThread_id());
						logger.debug("user_id = " + post.getUser_id());
						logger.debug("post_id = " + post.getPost_id());
						logger.debug("user_name = " + post.getUser_name());
						logger.debug("post_timestamp: = " + post.getPost_timestamp());
						logger.debug("post_content = " + post.getPost_content());

						// デバッグログの標準出力
						System.out.println("thread_id: =  " + post.getThread_id());
						System.out.println("user_id: =  " + post.getUser_id());
						System.out.println("post_id: =  " + post.getPost_id());
						System.out.println("user_name: =  " + post.getUser_name());
						System.out.println("post_timestamp: = : " + post.getPost_timestamp());
						System.out.println("post_content: = : " + post.getPost_content());
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// postListを返す
		return postList;
	}

	// 投稿IDを基に、親スレッドのスレッドIDを取得する。
	public int getThreadIdByPostId(int postId) throws SQLException {
		
		// 「threadId」初期化
		int threadId = 0;

		// SQLの準備
		String query = "SELECT thread_id FROM posts WHERE post_id = ?";

		// コネクション取得と、ステートメントを準備
		// コネクションとステートメントを確実にクローズする為、try-with-resourcesを実装。
		// DB接続処理後に、コネクションとステートメントを確実に解放する
		try (Connection connection = DatabaseConnectionManager.getConnection();
		     PreparedStatement ps = connection.prepareStatement(query)) {

			 // ステートメントの1番目の引数に、「postId」を指定する
		      ps.setInt(1, postId);


			// クエリの結果をResultSetの「resultSet」に代入する
			// 以下の処理が終了後、ResultSetに入っているリソースを確実に開放する為、try-with-resources構文を実装
			try (ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				threadId = rs.getInt("thread_id");
			}
		} catch (SQLException e) {
			logger.error("Error fetching thread_id by post_id", e);
		}
		return threadId;
	}

	}
}

	
