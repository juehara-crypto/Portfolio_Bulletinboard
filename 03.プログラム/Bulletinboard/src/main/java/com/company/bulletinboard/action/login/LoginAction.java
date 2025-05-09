package com.company.bulletinboard.action.login;

import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.UserDAO;
import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.validation.CustomValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionContext;
import java.util.Map;

import java.util.Map;

public class LoginAction extends BaseAction implements SessionAware {

	// ユーザーデータ用のフィールドを定義
	private String user_name;
	private String password;

	// セッション情報を保存するためのsessionマップを定義
	private Map<String, Object> session;

	// LoginActionクラス専用のLoggerインスタンス
	private static final Logger logger = LogManager.getLogger(LoginAction.class);

	// ログインフォームから送られて来たユーザーデータを保持するためのUserモデルオブジェクト
	private User user = new User();

	// Userオブジェクトのゲッターメソッド
	public User getUser() {
		return user;
	}

	// Userオブジェクトのセッターメソッド
	public void setUser(User user) {
		this.user = user;
	}

	// sessionマップをsetSessionメソッドを通じて、現在のセッション情報をアクションに渡す
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// user_nameフィールドのセッターメソッド
	// フォームから送信されたuser_nameの値がこのメソッドを通じてLoginActionに設定される
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	// passwordフィールドのセッターメソッド
	// フォームから送信されたpasswordの値がこのメソッドを通じてLoginActionに設定される
	public void setPassword(String password) {
		this.password = password;
	}

	// ★ユーザー名のバリデーション(特殊文字)
	// ユーザー名が「null」でなく、指定された特殊文字（@, #, $, %, &, *, !, ?, /, \, =, +, ^, |, <, >, {, }, [, ], ~）を一切含んでいないかをチェック
	// 「特殊文字を含まない」場合に true を返し、ログイン認証処理に進む
	// 処理がfalse(特殊文字を含む)だった場合は、後述の「validate()」メソッドに処理を渡す。
	public boolean validateUsername(String username) {
		return username != null && username.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
	}

	// ★パスワードのバリデーション(英字、数字、記号)
	// まずパスワードがnullでは無いことを確認する。これにより、passwordが未入力（null）の状態で正規表現のチェックを行わないようにしている。
	// matches()メソッドは、パスワードが特定のパターンに一致するかをチェックする。
	// (?=.*[a-zA-Z])にて、パスワードの中に少なくとも1つ以上の英字が含まれているかを確認
	// (?=.*\\d)にて、パスワードの中に少なくとも1つ以上の数字が含まれているかを確認
	// (?=.*[\\W_])にて、少なくとも1文字の記号が含まれているか（\\W は英数字以外の文字を指し、_ も含む）を確認
	// よってパスワードに「英字、数字、記号」が含んでいる場合は、true を返し、ログイン認証処理に進む
	// falseだった場合は、後述の「 validate()」メソッドに処理を渡す。
	public boolean validatePassword(String password) {
		return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).*$");
	}

	@Override
	public void validate() {

		if (user.getUser_name() == null || user.getUser_name().trim().isEmpty()) {
			// 空チェックはすでにされているので処理をスキップ
		} else {
			// ユーザー名の長さが有効な範囲内であれば、特殊文字バリデーションを実行
			if (!validateUsername(user.getUser_name())) {
				addFieldError("user.user_name", "ユーザー名には次の特殊文字を含めることはできません: @, #, $, %, &, *");
			}
		}

		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
		} else if (user.getPassword().length() < 8 || user.getPassword().length() > 64) {

		} else if (!validatePassword(user.getPassword())) {
			addFieldError("user.password", "パスワードには英字、数字、記号を含めてください。");
		}
	}

	// ユーザー認証処理と認証済みデータをセッションに保管する処理
	@Override
	public String mainProc() throws Exception {

		// デバッグ用のログ出力
		//		logger.debug("mainProc() called");
		//		logger.debug("ユーザーオブジェクト: {}", user);
		//		logger.debug("ユーザー名: {}", user.getUser_name());
		//		logger.debug("パスワード: {}", user.getPassword());

		// 必須項目チェックなど、フォームデータがバリデーションエラーかどうかを確認
		if (hasFieldErrors()) {
			// バリデーションエラーが発生した場合のログ出力と処理
			logger.error("バリデーションエラーが発生しました: {}", getFieldErrors());
			return INPUT;
		}

		// ユーザー認証処理
		User authenticatedUser = UserDAO.authenticate(user.getUser_name(), user.getPassword());

//		 logger.debug("認証結果: ユーザーID=" + authenticatedUser.getUser_id() +
//		 		", ユーザー名=" + authenticatedUser.getUser_name() +
//				", 権限タイプ=" + authenticatedUser.getAuth_type());

		// 認証結果の判定
		if (authenticatedUser != null) {
			logger.debug("認証成功: {}", authenticatedUser.getUser_name());
			logger.debug(
					"Authenticated User Details: user_id={}, user_name={}, password={}, auth_type={}, delete_flag={}, delete_day={}",
					authenticatedUser.getUser_id(),
					authenticatedUser.getUser_name(),
					authenticatedUser.getPassword(),
					authenticatedUser.getAuth_type(),
					authenticatedUser.getDelete_flag(),
					authenticatedUser.getDelete_day());

			// 認証済みユーザー情報をセッションに保管
			session.put("loggedInUser", authenticatedUser);

			//			// セッションに保存したユーザー情報をログ出力
			User sessionUser = (User) session.get("loggedInUser");
			if (sessionUser != null) {
				logger.debug(
						"Session User Details: user_id={}, user_name={}, password={}, auth_type={}, delete_flag={}, delete_day={}",
						sessionUser.getUser_id(),
						sessionUser.getUser_name(),
						sessionUser.getPassword(),
						sessionUser.getAuth_type(),
						sessionUser.getDelete_flag(),
						sessionUser.getDelete_day());
			} else {
				logger.error("セッションからユーザー情報を取得できませんでした。");
			}
			
		    // 認証済みのユーザー情報をthis.userにセット
		    this.user = authenticatedUser;

			// ユーザーの権限タイプに基づき適切な結果を返す
			if (authenticatedUser.getAuth_type() == 1) {
				logger.debug("管理者権限");
				logger.debug("認証結果: 管理者権限での処理を実行 (adminを返す)");
				return "admin";
			} else {
				logger.debug("一般ユーザー権限");
				logger.debug("認証結果: 一般ユーザー権限での処理を実行 (userを返す)");
				return "user";
			}
		} else {
						
			// 認証失敗時の処理
			logger.debug("認証失敗");
			addActionError("ユーザー名かパスワードが不正です。");
			return LOGIN;
		}
	}
}
