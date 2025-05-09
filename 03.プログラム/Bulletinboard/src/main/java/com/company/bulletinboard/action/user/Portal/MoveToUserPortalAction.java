package com.company.bulletinboard.action.user.Portal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import com.company.bulletinboard.interceptor.BaseAction;
import com.company.bulletinboard.model.User;

import com.company.bulletinboard.action.admin.bulletinboard.MoveBulletinboardManagementAction;
import com.company.bulletinboard.action.user.Portal.GetThreadAction;
import com.company.bulletinboard.action.user.Portal.GetThreadByIdAction;
import com.company.bulletinboard.action.user.Portal.GetPostByIdAction;

public class MoveToUserPortalAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	public String mainProc() {
		// セッションからログインユーザー情報を取得
		User loginUser = (User) session.get("loginUser");

		// 利用者ポータル画面の各リスト更新処理
		// 掲示板一覧の更新
		MoveBulletinboardManagementAction bulletinboardAction = new MoveBulletinboardManagementAction();
		bulletinboardAction.setSession(session);
		// bulletinboardAction.mainProc();

		// スレッド一覧の更新
		GetThreadAction threadAction = new GetThreadAction();
		threadAction.setSession(session);
		// threadAction.mainProc();

		// ユーザー個別スレッド一覧の更新
		GetThreadByIdAction threadByIdAction = new GetThreadByIdAction();
		threadByIdAction.setSession(session);
		// threadByIdAction.mainProc();

		// ユーザー個別投稿一覧の更新
		GetPostByIdAction postByIdAction = new GetPostByIdAction();
		postByIdAction.setSession(session);
		// postByIdAction.mainProc();

		return SUCCESS;
	}
}
