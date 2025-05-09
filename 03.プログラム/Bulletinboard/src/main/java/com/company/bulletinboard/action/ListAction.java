package com.company.bulletinboard.action;

// 掲示板管理の「キャンセルボタン」用アクションクラス。
// CancelActionからSUCCESSが返された後、リダイレクト先の処理として呼び出される。
// BoardDAOクラスの「getAllBoards()」メソッドを呼び出してデータベースから掲示板の全レコードを取得し、
// 結果を掲示板管理画面に返す。

import com.company.bulletinboard.model.User;
import com.company.bulletinboard.dao.BoardDAO;
import com.company.bulletinboard.interceptor.BaseAction;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	//  ListActionクラス専用のLoggerインスタンス
	private static final Logger logger = LogManager.getLogger(ListAction.class);

	// Userモデルクラスのbulletinboardsリストの定義
	private List<User> bulletinboards;

	// BaseActionクラスの「mainProc()」メソッドをオーバーライド
	@Override
	public String mainProc() throws Exception {
		logger.debug("ListAction mainProc started");

		try {
			logger.debug("Creating BoardDAO instance");

			// BoardDAOクラスのインタンスを生成
			BoardDAO dao = new BoardDAO();
			logger.debug("Fetching all boards from database");

			// getAllBoardsメソッドを呼び出して、データベースから掲示板の全レコードを取得し、
			// それをbulletinboardsリストに格納
			bulletinboards = dao.getAllBoards();

			logger.debug("Number of bulletin boards retrieved: " + bulletinboards.size());

			// bulletinboardsリスト内の各Userオブジェクトに対して、toStringメソッドを呼び出して
			// ログにその内容を出力する。これにより、各掲示板の詳細情報がログに記録される。
			for (User board : bulletinboards) {
				logger.debug("Board: " + board.toBulletinboardString());
			}
			logger.debug("ListAction mainProc finished");
		} catch (Exception e) {
			logger.error("Error in ListAction mainProc", e);
			throw e;
		}
		System.out.println("ListAction execute started");
		// 処理成功時に「SUCCESS」を返す
		return SUCCESS;
	}

	/**
	 * bulletinboardsフィールドの値を返すゲッターメソッド。
	 * 
	 * @return bulletinboardsフィールドの値
	 */
	public List<User> getBulletinboards() {
		return bulletinboards;
	}

	/**
	 * bulletinboardsフィールドの値を設定するセッターメソッド。
	 * 
	 * @param bulletinboards 新しいbulletinboardsフィールドの値
	 */
	public void setBulletinboards(List<User> bulletinboards) {
		this.bulletinboards = bulletinboards;
	}

}
