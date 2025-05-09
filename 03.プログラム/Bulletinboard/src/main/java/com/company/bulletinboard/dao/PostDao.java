package com.company.bulletinboard.dao;

// PostDaoクラスの処理内容
// ①投稿データをデータベースに保存する処理
//   save(User post)メソッド：PostActionクラスから呼び出され、「投稿データをデータベースに保存する」ためのメソッド
// ②投稿データを取得する処理
//   getPostsByThreadId(int thread_id)メソッド：投稿一覧表示用のメソッドThreadDetailActionクラスから呼び出され、
//  「投稿データをリストで取得」する
// ③指定されたthread_idに関連する投稿件数を取得する処理
//   掲示板詳細画面の初期表示時に、BoardDetailActionクラスからgetPostCountByThreadIdメソッドが呼び出される。
//   指定されたthread_idに関連する投稿件数を取得し、postCount変数に代入する。処理結果を返す。

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

public class PostDao {

	// int connection;

	// PostDaoクラス用のデバック処理。ロガーインスタンスの生成
	private static final Logger logger = LogManager.getLogger(PostDao.class);

	// 投稿データをデータベースに保存するメソッド
	public void save(User post) {

		// デバッグ処理の開始ログを記録
		logger.info("PostDao: mainProc() start");
		;
		String sql = "INSERT INTO posts (post_content, thread_id, user_id, post_delete_flag, post_delete_day, post_timestamp) "
				+
				"VALUES (?, ?, ?, 0, NULL, NOW())";

		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {

			logger.debug("Connection opened: " + connection);

			pstmt.setString(1, post.getPost_content());
			pstmt.setInt(2, post.getThread_id());
			pstmt.setInt(3, post.getUser_id());
			pstmt.executeUpdate();

			logger.debug("Post saved successfully.");

			logger.debug("Post_content: ID = " + post.getPost_content());
			logger.debug("Thread_ID: = " + post.getThread_id());
			logger.debug("User_id: ID = " + post.getUser_id());

			// デバッグログの表示
			System.out.println("Post_content: ID =  " + post.getPost_content());
			System.out.println("Thread_ID: =  " + post.getThread_id());
			System.out.println("User_id: ID = : " + post.getUser_id());

			logger.debug("Resources are about to be closed: " + connection);
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			logger.debug("Connection closing.");
		}
	}

	// 投稿一覧表示用のメソッド
	public List<User> getPostsByThreadId(int thread_id) {

		// 投稿データ用のUser型アレイリストを定義
		List<User> postList = new ArrayList<>();

		// JDBCを使用してデータベース接続を設定
		try (Connection connection = DatabaseConnectionManager.getConnection()) {

			// postsテーブルとusersテーブルを結合して、特定のスレッドに関連する投稿とその投稿者のユーザー名を取得するためのクエリ
			String query = "SELECT p.*, u.user_name FROM posts p JOIN users u ON p.user_id = u.user_id WHERE p.thread_id = ? ORDER BY p.post_timestamp ASC";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, thread_id);

				// クエリ結果をresultSetに保存
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {

						// User型のpostオブジェクトを生成
						User post = new User();

						// postオブジェクトにクエリ結果を保存
						post.setUser_id(resultSet.getInt("user_id"));
						post.setPost_id(resultSet.getInt("post_id"));
						post.setUser_name(resultSet.getString("user_name"));
						post.setPost_timestamp(resultSet.getTimestamp("post_timestamp"));
						post.setPost_content(resultSet.getString("post_content"));
						postList.add(post);

						// デバッグ処理
						logger.debug("user_id = " + post.getUser_id());
						logger.debug("post_id = " + post.getPost_id());
						logger.debug("user_name = " + post.getUser_name());
						logger.debug("post_timestamp: = " + post.getPost_timestamp());
						logger.debug("post_content = " + post.getPost_content());

						// デバッグログの標準出力
						// System.out.println("user_id: =  " + post.getUser_id());
						// System.out.println("post_id: =  " + post.getPost_id());
						// System.out.println("user_name: =  " + post.getUser_name());
						// System.out.println("post_timestamp: = : " + post.getPost_timestamp());
						// System.out.println("post_content: = : " + post.getPost_content());
					}
				}

			}
			logger.debug("Resources are about to be closed: " + connection);

		} catch (SQLException e) {
			logger.error("エラー発生: ", e);
		}

		// postListを返す
		return postList;
	}

	// 指定されたthread_idに関連する投稿件数を取得するメソッド
	public int getPostCountByThreadId(int thread_id) {

		int postCount = 0;

		// SQLを生成
		// posts テーブルから thread_id が一致し、かつ post_delete_flag が 0 である行数をカウントしています。
		// post_delete_flag が 1 の投稿は削除されたものとみなし、カウントから除外する。
		String sql = "SELECT COUNT(*) FROM posts WHERE thread_id = ? AND post_delete_flag = 0";

		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			//try (PreparedStatement statement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, thread_id);

			// クエリ結果をresultSetに保存
			try (ResultSet rs = preparedStatement.executeQuery()) {
				if (rs.next()) {
					postCount = rs.getInt(1);
					logger.debug("Post count for thread_id " + thread_id + ": " + postCount);
				}
			}

		} catch (SQLException e) {

			logger.error("Error while fetching post count for thread_id " + thread_id, e);
			logger.error("Error closing connection", e);
		}
		// logger.debug("Resources are about to be closed: " + connection);

		return postCount;
	}
}
