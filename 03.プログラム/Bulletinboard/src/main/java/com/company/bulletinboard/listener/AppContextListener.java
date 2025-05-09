package com.company.bulletinboard.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.company.bulletinboard.dao.DatabaseConnectionManager;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // アプリケーションの起動時にモニタリングを開始
        DatabaseConnectionManager.startMonitoring();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // DatabaseConnectionManagerのインスタンスを生成して、非staticメソッドを呼び出す
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        connectionManager.closeDataSource();
    }
}
