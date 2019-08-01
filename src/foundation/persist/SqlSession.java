package foundation.persist;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;


public class SqlSession {

	private static Logger logger;
	private static DruidDataSource dataSource;
	
	private Connection connection;
	private boolean inTrans;
	
	static {
		logger = Logger.getLogger(SqlSession.class);
	}
	
	private SqlSession(Connection conn) {
		connection = conn;
		inTrans = false;
	}
	
	static {
		logger = Logger.getLogger("com.oval.datatrans.resource.db");
	}

	public static Connection createConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
	}
	
	public static SqlSession getInstance() {
		Connection connection = createConnection();
		SqlSession instance = new SqlSession(connection);
		return instance;
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			}
			catch (Exception e) {
			}
		}
	}

	public void beginTransaction() throws SQLException {
		inTrans = true;
		
		if (connection != null) {
			connection.setAutoCommit(false);
		}
	}
	
	public boolean inTransaction() {
		return inTrans;
	}
	
	public void commit() throws SQLException {
		if (inTrans) {
			inTrans = false;
			doCommit();
		}
	}

	public void rollback() throws SQLException {
		if (inTrans) {
			inTrans = false;
			doRollBack();
		}
	}
	
	private void doCommit() throws SQLException {
		try {
			connection.commit();
		}
		finally {
			connection.close();
			connection = null;
		}
	}

	private void doRollBack() throws SQLException {
		try {
			connection.rollback();
		}
		finally {
			connection.close();
			connection = null;
		}
	}

	public Connection getConnection() throws SQLException {
		if (connection != null) {
			return connection;
		}

		connection = createConnection();
		connection.setAutoCommit(!inTrans);
		
		return connection;
	}
	
	public static DruidDataSource appendDataSource(String name) throws SQLException {
		dataSource = new DruidDataSource();
		
        dataSource.setInitialSize(1);
        dataSource.setFilters("stat,log4j");
        dataSource.setMaxWait(60000);
        dataSource.setMinIdle(1);
        dataSource.setTimeBetweenEvictionRunsMillis(3000);
        dataSource.setMinEvictableIdleTimeMillis(3000 * 500);
        dataSource.setRemoveAbandonedTimeout(300);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        
		return dataSource;
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

}
