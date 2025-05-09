package com.company.bulletinboard.model;

import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User {
	private static final Logger logger = LogManager.getLogger(User.class);

	private int user_id;
	private String user_name;
	private String password;
	private int auth_type;
	private int delete_flag;
	private String delete_day;

	// 以下追加「bulletinboard」テーブル用のカラム
	private int bulletinboard_id;
	private String bulletinboard_title;
	private String bulletinboard_content;
	private int bulletinboard_delete_flag;
	private String bulletinboard_delete_day;
	private String bulletinboard_creation_day;

	// 以下追加「threads」テーブル用のカラム
	private int thread_id;
	private String thread_title;
	private int thread_delete_flag;
	private String thread_delete_day;

	// 以下追加「posts」テーブル用のカラム
	private int post_id;
	private String post_content;
	private int post_delete_flag;
	private String post_delete_day;
	private Date post_timestamp;

	// 投稿カウント用のフィールド
	private int post_count;

	public User() {
	}

	public User(int user_id, String user_name, int auth_type) {
		this.user_id = user_id;
		this.user_name = user_name;
		this.auth_type = auth_type;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAuth_type() {
		return auth_type;
	}

	public void setAuth_type(int auth_type) {
		this.auth_type = auth_type;
	}

	public int getDelete_flag() {
		return delete_flag;
	}

	public void setDelete_flag(int delete_flag) {
		this.delete_flag = delete_flag;
	}

	public String getDelete_day() {
		return delete_day;
	}

	public void setDelete_day(String delete_day) {
		this.delete_day = delete_day;
	}

	// 以下、「bulletinboard」テーブル用のカラム

	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	public String getBulletinboard_title() {
		return bulletinboard_title;
	}

	public void setBulletinboard_title(String bulletinboard_title) {
		this.bulletinboard_title = bulletinboard_title;
	}

	public String getBulletinboard_content() {
		return bulletinboard_content;
	}

	public void setBulletinboard_content(String bulletinboard_content) {
		this.bulletinboard_content = bulletinboard_content;
	}

	public int getBulletinboard_delete_flag() {
		return bulletinboard_delete_flag;
	}

	public void setBulletinboard_delete_flag(int bulletinboard_delete_flag) {
		this.bulletinboard_delete_flag = bulletinboard_delete_flag;
	}

	public String getBulletinboard_delete_day() {
		return bulletinboard_delete_day;
	}

	public void setBulletinboard_delete_day(String bulletinboard_delete_day) {
		this.bulletinboard_delete_day = bulletinboard_delete_day;
	}
	
    public String getBulletinboard_creation_day() {
        return bulletinboard_creation_day;
    }

    public void setBulletinboard_creation_day(String bulletinboard_creation_day) {
        this.bulletinboard_creation_day = bulletinboard_creation_day;
    }

	

	// 以下、「threads」テーブル用のカラム
	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	public String getThread_title() {
		return thread_title;
	}

	public void setThread_title(String thread_title) {
		this.thread_title = thread_title;
	}

	public int getThread_delete_flag() {
		return thread_delete_flag;
	}

	public void setThread_delete_flag(int thread_delete_flag) {
		this.thread_delete_flag = thread_delete_flag;
	}

	public String getThread_delete_day() {
		return thread_delete_day;
	}

	public void setThread_delete_day(String thread_delete_day) {
		this.thread_delete_day = thread_delete_day;
	}

	// 以下、「posts」テーブル用のカラム

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public int getPost_delete_flag() {
		return post_delete_flag;
	}

	public void setPost_delete_flag(int post_delete_flag) {
		this.post_delete_flag = post_delete_flag;
	}

	public String getPost_delete_day() {
		return post_delete_day;
	}

	public void setPost_delete_day(String post_delete_day) {
		this.post_delete_day = post_delete_day;
	}

	public Date getPost_timestamp() {
		return post_timestamp;
	}

	public void setPost_timestamp(Date post_timestamp) {
		this.post_timestamp = post_timestamp;
	}

	// このメソッドでは、投稿内容に含まれる「>>数字」 形式の部分をHTMLリンクに変換します。
	// これにより、ユーザーが特定の投稿を参照するためのリンクを作成できる。
	// また、変換された内容をデバッグログに出力することで、変換処理が正しく行われたかを確認できる。
	// post_content が null の場合は、空の文字列を返すことでエラーを防ぐことができる。
	public String getPostContent() {
		if (post_content != null) {
			String transformedContent = post_content.replaceAll(">>([0-9]+)", "<a href=\"#post_$1\">>>$1</a>");
			logger.debug("Transformed post_content: " + transformedContent); // デバッグログを追加
			return transformedContent;
		}
		return "";
	}

	public String getFormattedPostTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 (E) HH時mm分");
		String formattedTimestamp = sdf.format(post_timestamp);

		// デバッグログの追加
		logger.debug("Formatted post_timestamp: " + formattedTimestamp);

		return formattedTimestamp;
	}

	// 掲示板管理用のtoStringメソッド(デバッグ用)
	public String toBulletinboardString() {
		return "User{" +
				"bulletinboard_id=" + bulletinboard_id +
				", bulletinboard_title='" + bulletinboard_title + '\'' +
				", bulletinboard_content='" + bulletinboard_content + '\'' +
				", bulletinboard_delete_flag=" + bulletinboard_delete_flag +
				", bulletinboard_delete_day='" + bulletinboard_delete_day + '\'' +
				'}';
	}

	// ユーザー管理用のtoStringメソッド(デバッグ用)
	 public String toUserString() {
		return "User{" +
				"user_id=" + user_id +
				", user_name='" + user_name + '\'' +
				", password='" + password + '\'' +
				", auth_type=" + auth_type +
				", delete_flag=" + delete_flag +
				", delete_day='" + delete_day + '\'' +
				'}';
	 }

	// スレッド用のtoStringメソッド(デバッグ用)
	public String toThreadString() {
		return "User{" +
				"thread_id=" + thread_id +
				", thread_title='" + thread_title + '\'' +
				", bulletinboard_id='" + bulletinboard_id + '\'' +
				", user_id=" + user_id +
				", thread_delete_flag=" + thread_delete_flag +
				", thread_delete_day='" + thread_delete_day + '\'' +
				'}';
	}

	// ObjectクラスのtoStringメソッドをオーバーライド
	@Override
	public String toString() {
		return toUserString(); // デフォルトではユーザー管理用の文字列を返す
	}

	// 投稿カウント用フィールドのゲッター
	public int getPost_count() {
		return post_count;
	}

	// 投稿カウント用フィールドのセッター
	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

}