package com.company.bulletinboard.action.user.Portal;

// MoveCreateThreadActionクラスから、処理がリダイレクションされbulletinboard_idパラメータが引き継がれる
// HTTPリクエストのURLパラメータとして渡されたbulletinboard_idを文字列から整数に変換して、クラスのフィールド
// bulletinboard_idにセットし、値を保持する。
// 値を保持し、「SUCCESS」を返す

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.company.bulletinboard.interceptor.BaseAction;

public class CreateThreadScreenAction extends BaseAction {

	// デバッグ処理用
	private static final Logger logger = LogManager.getLogger(CreateThreadScreenAction.class);

	// 掲示板ID保持用のフィールド
	private int bulletinboard_id;

	// セッターで取得された掲示板IDを、取り出す為のゲッターメソッド
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// MoveCreateThreadActionから引き継がれた掲示板IDをセット
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	@Override
	public String mainProc() {

		logger.info("CreateThreadScreenAction: mainProc() start");

		// リクエストパラメータからbulletinboard_idを取得し、整数に変換
		bulletinboard_id = Integer.parseInt(request.getParameter("bulletinboard_id"));

		logger.debug("bulletinboard_id: ID = " + bulletinboard_id);

		System.out.println("Received Bulletinboard ID in CreateThreadScreenAction: " + bulletinboard_id);

		// bulletinboard_idが無効な場合はエラーを返す
		if (bulletinboard_id <= 0) {
			addActionError("Invalid bulletinboard ID: " + bulletinboard_id);
			return ERROR;
		}

		return SUCCESS;
	}
}