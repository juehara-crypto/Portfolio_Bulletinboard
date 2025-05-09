package com.company.bulletinboard.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnectionManager {

	// ロガーを定義
	private static final Logger logger = LogManager.getLogger(DatabaseConnectionManager.class);

	// HikariCP用のデータソースを定義
	private static HikariDataSource dataSource;

	// スケジューラ
	private static Timer timer;

	// モニタリングタイマーをインスタンス変数として定義
	private Timer monitoringTimer;

	// コンストラクタでタイマーを初期化
	public DatabaseConnectionManager() {
		monitoringTimer = new Timer();
	}

	// 静的イニシャライザでHikariCPの設定を行う
	static {
		try {
            // プロパティファイルを読み込む
            Properties properties = new Properties();
            try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream("db-config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find db-config.properties");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Error reading db-config.properties", e);
            }

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
			
         // HikariCPの設定
			HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
			config.setDriverClassName("com.mysql.cj.jdbc.Driver");
			config.setMaximumPoolSize(10); // プール内の最大接続数
			config.setIdleTimeout(30000); // アイドル接続がクローズされるまでの時間
			config.setMaxLifetime(1800000); // 接続の最大寿命

			// HikariDataSourceの作成
			dataSource = new HikariDataSource(config);
			logger.debug("HikariCP DataSource initialized");
		} catch (Exception e) {
			logger.error("Error initializing HikariCP DataSource", e);
			throw new RuntimeException("Error initializing HikariCP DataSource", e);
		}
	}

	/**
	 * データベースコネクションを取得するメソッド。
	 * コネクション取得時にログを出力する。
	 */
	public static Connection getConnection() throws SQLException {
		try {
			// HikariCPからコネクションを取得
			Connection realConnection = dataSource.getConnection();
			logger.debug("Connection acquired: " + realConnection);

			// Connection を LoggingConnectionWrapper でラップして返す
			// Connection wrappedConnection = new LoggingConnectionWrapper(realConnection);
			// logger.debug("Wrapped Connection created: " + wrappedConnection);

			// return wrappedConnection;
			return realConnection;

		} catch (SQLException e) {
			logger.error("Failed to acquire connection", e);
			throw e;
		}
	}

	/**
	 * コネクションを閉じるメソッド。
	 * コネクション解放時にログを出力する。
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				logger.debug("Connection closed: " + conn);
			} catch (SQLException e) {
				logger.error("Failed to close connection", e);
			}
		}
	}

	/**
	 * データソースをクローズするメソッド。
	 * アプリケーション終了時などに呼び出し、リソースを解放する。
	 */

	// アプリケーションがシャットダウンする際にのみ出力される
	public void closeDataSource() {
		logger.debug("Entering closeDataSource method.");

		try {
			// データソースのクローズ処理
			if (dataSource != null) {
				logger.debug("Closing data source...");
				dataSource.close();
				logger.debug("Data source closed.");
			} else {
				logger.debug("Data source is null, nothing to close.");
			}
		} catch (Exception e) {
			logger.error("Error closing data source.", e);
		} finally {
			// 必要に応じて、タイマーの停止など追加のクリーンアップ処理
			logger.debug("Stopping monitoring timer...");
			monitoringTimer.cancel();
			logger.debug("Monitoring timer stopped.");
		}

		logger.debug("Exiting closeDataSource method.");
	}

	/**
	 * HikariCPの接続プールの状態をモニタリングするメソッド。
	 * 定期的に接続プールの状態をログに出力する。
	 */
	public static void startMonitoring() {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (dataSource != null && !dataSource.isClosed()) {
					logger.info("Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
					logger.info("Idle connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
					logger.info("Total connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
					logger.info("Threads awaiting connection: "
							+ dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
				}
			}
		}, 0, 5000); // 5秒ごとにログ出力
	}
}