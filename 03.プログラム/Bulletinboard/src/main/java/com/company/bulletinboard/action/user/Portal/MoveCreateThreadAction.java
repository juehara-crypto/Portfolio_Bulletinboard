package com.company.bulletinboard.action.user.Portal;

// MoveCreateThread アクションにリダイレクトされたとき、MoveCreateThreadAction クラスでURLパラメータとして渡された
// bulletinboard_id を取得します。
// 処理が正常であれば、CreateThreadScreenにリダイレクションされます。

import com.company.bulletinboard.interceptor.BaseAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveCreateThreadAction extends BaseAction {

	// 掲示板ID保持用のフィールドの定義
	private int bulletinboard_id;

	// デバッグ処理
	private static final Logger logger = LogManager.getLogger(MoveCreateThreadAction.class);

	// セッターで取得した掲示板IDを取り出すためのゲッターメソッド 
	public int getBulletinboard_id() {
		return bulletinboard_id;
	}

	// URLパラメータのbulletinboard_idをセット
	public void setBulletinboard_id(int bulletinboard_id) {
		this.bulletinboard_id = bulletinboard_id;
	}

	// 取得したbulletinboard_idを、ログに記録する。
	// bulletinboard_idが0または無効な値であれば、エラーを返す。
	// 正常なbulletinboard_idがあれば、「SUCCESS」を返し、次のアクションに進む。
	@Override
	public String mainProc() throws Exception {
		logger.debug("Form Parameter bulletinboard_id: " + bulletinboard_id);

		//bulletinboard_idが0または無効な値であれば、エラーを返します。
		if (bulletinboard_id == 0) {
			logger.error("Invalid bulletinboard_id format: " + bulletinboard_id);
			return ERROR;
		}

		logger.debug("Received Bulletinboard ID in MoveCreateThreadAction: " + bulletinboard_id);

		if (bulletinboard_id <= 0) {
			addActionError("Invalid bulletinboard ID: " + bulletinboard_id);
			return ERROR;
		}

		return SUCCESS;
	}

}
