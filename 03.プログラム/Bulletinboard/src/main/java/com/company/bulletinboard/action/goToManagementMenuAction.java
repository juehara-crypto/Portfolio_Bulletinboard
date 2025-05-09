package com.company.bulletinboard.action;

import com.company.bulletinboard.interceptor.BaseAction;

public class goToManagementMenuAction extends BaseAction {

	/**
	 * メイン処理
	 * 掲示板作成画面への遷移処理
	 * @return 処理結果の文字列
	 * @throws Exception 
	 */
	@Override
	public String mainProc() {
		// 成功時は "success" を返す
		return "success";
	}
}
