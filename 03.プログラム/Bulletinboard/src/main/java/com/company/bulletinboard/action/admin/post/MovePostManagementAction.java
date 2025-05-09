package com.company.bulletinboard.action.admin.post;

import com.company.bulletinboard.interceptor.BaseAction;

// 投稿管理作成中
public class MovePostManagementAction extends BaseAction {

	@Override
	public String mainProc() {
		// 成功時は "success" を返す
		return "success";
	}
}
