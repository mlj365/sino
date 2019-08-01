package foundation.persist.sql;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.IMetaDataLoader;
import foundation.persist.SqlSession;
import foundation.persist.loader.EntityLoader;
import foundation.persist.loader.EntitySetLoader;
import foundation.persist.loader.ListLoader;
import foundation.persist.loader.ObjectLoader;
import foundation.persist.loader.PrimaryKeyLoader;
import foundation.persist.loader.ValueLoader;
import foundation.util.Util;


public class SQLRunner {
	
	protected static Logger logger;
	protected static ThreadLocal<Connection> activeConn;
	private static Set<String> tableSet;

	static {
		logger = Logger.getLogger(SQLRunner.class);
		tableSet = new HashSet<String>();
		activeConn = new ThreadLocal<Connection>();
	}
	
	public static synchronized void beginTrans() throws SQLException {
		Connection conn = activeConn.get();
		
		if (conn == null) {
			conn = SqlSession.createConnection();
			conn.setAutoCommit(false);
			activeConn.set(conn);
		}
	}
	
	public static void commit() throws Exception {
		Connection conn = activeConn.get();
		
		if (conn == null) {
			throw new Exception("empty conn, can not commit!");
		}
		
		try {
			conn.commit();
		}
		finally {
			try {
				conn.close();
			}
			catch (Exception e) {
			}
			activeConn.remove();	
		}
	}
	
	public static void rollback() throws Exception {
		Connection conn = activeConn.get();
		
		if (conn == null) {
			throw new Exception("empty conn, can not commit!");
		}
		
		try {
			conn.rollback();
		}
		finally {
			try {
				conn.close();
			}
			catch (Exception e) {
			}
			activeConn.remove();	
		}		
	}
	
	public static int execSQL(String sql) throws Exception {
		NamedSQL namedSQL = new NamedSQL("temp", sql);
		return execSQL(null, namedSQL);
	}
	
	public static int execSQL(NamedSQL sql) throws Exception {
		return execSQL(null, sql);
	}
	
	public static int execSQL(NamedSQLSet sqlSet) throws Exception {
		int result = 0;
		
		Connection conn = SqlSession.createConnection();
		try {
			conn.setAutoCommit(false);
			result = execSQL(conn, sqlSet);
			conn.commit();
			return result;
		} 
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public static int execSQL(Connection conn, NamedSQL sql) throws Exception {
		 return execSQL(conn, sql.getSQLString());
	}
	
	public static int execSQL(Connection conn, NamedSQLSet sqlSet) throws Exception {
		int result = 0;
		
		for (NamedSQL namedSQL: sqlSet) {
			result = result + execSQL(conn, namedSQL.getSQLString());
		}
		
		return result;
	}
	
	public static boolean execProcedure(Connection conn, String sql) throws Exception {
		CallableStatement stmt = null;
		boolean result = false;
		
		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;

		try {
			if (conn == null) {
				conn = SqlSession.createConnection();
			}
			
			if (conn != null) {
				logger.debug(sql);
				
				stmt  = conn.prepareCall(sql);
				Date now = new Date(); 
				//注册存储过程的第个参数
				stmt.setDate(1, new java.sql.Date(now.getTime()));  
				//注册存储过程的第二个参数  
				stmt.registerOutParameter(2,java.sql.Types.BIT); 
				stmt.setBoolean(2, true); 
				stmt.execute();
				result = stmt.getBoolean(2);
			}
		} 
		catch (SQLException e) {
			onError(sql, e);
			throw e;
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
			} finally {
				if (closeFlag && conn != null) {
					conn.close();
				}
			}
		}
		return result;
	}
	
	public static int execSQL(Connection conn, String sql) throws Exception {
		Statement stmt = null;
		int result = 0;
		
		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;

		try {
			if (conn == null) {
				conn = SqlSession.createConnection();
			}
			
			if (conn != null) {
				logger.debug(sql);
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
				result = stmt.executeUpdate(sql);
			}
		} 
		catch (SQLException e) {
			onError(sql, e);
			throw e;
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
			} finally {
				if (closeFlag && conn != null) {
					conn.close();
				}
			}
		}
		return result;
	}
	
	public static void getData(NamedSQL namedSQL, ILoadable loadable, Object ...args) throws Exception {
		getData(null, namedSQL, loadable, args);
	}
	
	public static void getData(NamedSQL namedSQL, IStepLoadable loadable, Object ...args) throws Exception {
		getData(null, namedSQL, loadable, args);
	}
	
	public static void getData(Connection conn, NamedSQL namedSQL, ILoadable loadable, Object ...args) throws Exception {
		ResultSet rslt = null;
		Statement stmt = null;

		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;
		String sql = namedSQL.getSQL();
		
		if (sql != null) {
			sql.trim();
			
			if (!"".equals(sql)) {
				try {
					if (conn == null) {
						conn = SqlSession.createConnection();
					}

					if (conn != null) {
						logger.debug(sql);
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						rslt = stmt.executeQuery(sql);
						
						loadable.load(rslt, args);
					}
					else{
						throw new SQLException("can not get coinnection");
					}
				} 
				catch (SQLException e) {
					logger.error("getDataFromDB error:" + e.getMessage());
					throw e;
				} 
				finally {
					try {
						if (rslt != null) {
							rslt.close();
						}
					} catch (SQLException e) {
					}
					finally {
						try {
							if (stmt != null) {
								stmt.close();
							}
						} catch (SQLException e) {
						}
						finally {
							if (closeFlag && conn != null) {
								conn.close();
							}
						}
					}
				}
			}
		}
	}
	
	public static void getData(Connection conn, NamedSQL namedSQL, IStepLoadable loadable, Object ...args) throws Exception {
		ResultSet rslt = null;
		PreparedStatement stmt = null;

		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;
		String sql = namedSQL.getSQLString();
		
		try {
			if (conn == null) {
				conn = SqlSession.createConnection();
			}

			if (conn != null) {
				while (loadable.hasNextForLoad()) {
					stmt = conn.prepareStatement(sql);
					loadable.setLoadParameters(stmt);
					
					try {
						try {
							rslt = stmt.executeQuery();
							loadable.load(rslt, args);
						}
						finally {
							if (rslt != null) {
								rslt.close();
							}
						}						
					}
					finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
			else{
				throw new SQLException("can not get coinnection");
			}
		} 
		catch (SQLException e) {
			logger.debug(sql);
			logger.error("getDataFromDB error:" + e.getMessage());
			throw e;
		} 
		finally {
			if (closeFlag && conn != null) {
				conn.close();
			}
		}
	}
	
	public static void saveData(NamedSQL namedSQL1, NamedSQL namedSQL2, IDoubleSavable savable, Object ...args) throws Exception {
		saveData(null, namedSQL1, namedSQL2, savable, args);
	}
	
	public static void saveData(NamedSQL namedSQL, ISavable savable, Object ...args) throws Exception {
		saveData(null, namedSQL, savable, args);
	}
	
	public static void saveData(Connection conn, NamedSQL namedSQL, ISavable savable, Object ...args) throws Exception {
		ResultSet rslt = null;
		PreparedStatement stmt = null;

		String sql = namedSQL.getSQLString();
		
		if (sql != null) {
			sql = sql.trim();
			
			if (conn == null) {
				conn = activeConn.get();
			}
			boolean closeFlag = conn == null;
			
			if (!"".equals(sql)) {
				try {
					if (conn == null) {
						conn = SqlSession.createConnection();
					}
					
					if (conn != null) {
						logger.debug(sql);
						stmt = conn.prepareStatement(sql);
						savable.save(stmt, args);
					}
					else{
						throw new SQLException("can not get coinnection");
					}
				} 
				catch (SQLException e) {
					logger.error("getDataFromDB error:" + e.getMessage());
					throw e;
				} finally {
					try {
						if (rslt != null) {
							rslt.close();
						}
					} catch (SQLException e) {
					}
					finally {
						try {
							if (stmt != null) {
								stmt.close();
							}
						} catch (SQLException e) {
						}
						finally {
							if (closeFlag && conn != null) {
								conn.close();
							}
						}
					}
				}
			}
		}
	}
	
	public static void saveData(Connection conn, NamedSQL namedSQL1, NamedSQL namedSQL2, IDoubleSavable savable, Object ...args) throws Exception {
		PreparedStatement stmt1 = null, stmt2 = null;

		String sql1 = namedSQL1.getSQLString();
		String sql2 = namedSQL2.getSQLString();
		
		if (!Util.isEmptyStr(sql1) && !Util.isEmptyStr(sql2)) {
			if (conn == null) {
				conn = activeConn.get();
			}
			boolean closeFlag = conn == null;
			
			try {
				if (conn == null) {
					conn = SqlSession.createConnection();
				}
					
				if (conn != null) {
					logger.debug(sql1);
					logger.debug(sql2);
					
					stmt1 = conn.prepareStatement(sql1);
					stmt2 = conn.prepareStatement(sql2);
					
					savable.save(stmt1, stmt2, args);
				}
				else{
					throw new SQLException("can not get coinnection");
				}
			} 
			catch (SQLException e) {
				logger.error("getDataFromDB error:" + e.getMessage());
				throw e;
			} finally {
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (SQLException e) {
				}
				finally {
					try {
						if (stmt2 != null) {
							stmt2.close();
						}
					} catch (SQLException e) {
					}
					finally {
						if (closeFlag && conn != null) {
							conn.close();
						}
					}
				}
			}
		}
	}
	
	public static void saveData(NamedSQL namedSQL, IStepSavable stepSavable, Object ...args) throws Exception {
		saveData(null, namedSQL, stepSavable, args);
	}
	
	public static void saveData(Connection conn, NamedSQL namedSQL, IStepSavable stepSavable, Object ...args) throws Exception {
		PreparedStatement stmt = null;

		String sql = namedSQL.getSQLString();
		
		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;
		
		if (sql != null) {
			sql.trim();
			
			if (!"".equals(sql)) {
				try {
					if (conn == null) {
						conn = SqlSession.createConnection();
					}
					
					if (conn != null) {
						while (stepSavable.hasNextForSave()) {
							stmt = conn.prepareStatement(sql);
							
							try {
								stepSavable.save(stmt, args);
							}
							finally {
								if (stmt != null) {
									stmt.close();
								}
							}
						}
					}
					else{
						throw new SQLException("can not get coinnection");
					}
				} 
				catch (SQLException e) {
					logger.debug(sql);
					logger.error("getDataFromDB error:" + e.getMessage());
					throw e;
				} 
				finally {
					if (closeFlag && conn != null) {
						conn.close();
					}
				}
			}
		}
	}
	
	public static EntitySet getEntitySet(NamedSQL namedSQL) throws Exception {
		return getEntitySet(null, namedSQL);
	}
	
	public static EntitySet getEntitySet(Connection conn, NamedSQL namedSQL) throws Exception {
		EntitySet result = null;
		
		EntitySetLoader entitySetLoader = new EntitySetLoader(namedSQL.getName());
		getData(conn, namedSQL, entitySetLoader);
		result = entitySetLoader.getDataSet();
		
		return result;
	}
	
	public static <T> List<T> getList(NamedSQL namedSQL, Class<T> clazz) throws Exception {
		return getList(null, namedSQL, clazz);
	}
	
	public static <T> List<T> getList(Connection conn, NamedSQL namedSQL, Class<T> clazz) throws Exception {
		List<T> result = null;
		
		ListLoader<T> listLoader = new ListLoader<T>(namedSQL.getName(), clazz);
		getData(conn, namedSQL, listLoader);
		result = listLoader.getList();
		
		return result;
	}	
	
	public static Entity getEntity(NamedSQL namedSQL) throws Exception {
		return getEntity(null, namedSQL);
	}
	
	public static Entity getEntity(Connection conn, NamedSQL namedSQL) throws Exception {
		Entity result = null;
		
		EntityLoader entityLoader = new EntityLoader(namedSQL.getName());
		getData(conn, namedSQL, entityLoader);
		result = entityLoader.getEntity();
		
		return result;
	}
	
	public static Object getObject(NamedSQL namedSQL, Class<?> clazz) throws Exception {
		return getObject(null, namedSQL, clazz);
	}

	public static Object getObject(Connection conn, NamedSQL namedSQL, Class<?> clazz) throws Exception {
		Object result = null;
		
		ObjectLoader objectLoader = new ObjectLoader(namedSQL.getName(), clazz);
		getData(conn, namedSQL, objectLoader);
		result = objectLoader.getObject();
		
		return result;
	}
	
	public static String getString(NamedSQL namedSQL) throws Exception {
		return getString(null, namedSQL, 1);
	}
	
	public static String getString(Connection conn, NamedSQL namedSQL, int idx) throws Exception {
		String result = null;
		
		ValueLoader valueLoader = new ValueLoader();
		getData(conn, namedSQL, valueLoader);
		result = valueLoader.getString();
		
		return result;
	}
	
	public static int getInteger(NamedSQL namedSQL) throws Exception {
		return getInteger(null, namedSQL);
	}
	
	public static int getInteger(Connection conn, NamedSQL namedSQL) throws Exception {
		int result = 0;
		
		ValueLoader valueLoader = new ValueLoader();
		getData(conn, namedSQL, valueLoader);
		result = valueLoader.getInt();
		
		return result;
	}
	
	public static BigDecimal getBigDecimal(NamedSQL namedSQL) throws Exception {
		return getBigDecimal(null, namedSQL);
	}
	
	public static BigDecimal getBigDecimal(Connection conn, NamedSQL namedSQL) throws Exception {
		BigDecimal result = null;
		
		ValueLoader valueLoader = new ValueLoader();
		getData(conn, namedSQL, valueLoader);
		result = valueLoader.getBigDecimal();
		
		return result;
	}
	
	public static Result getResult(NamedSQL namedSQL) throws Exception {
		return getResult(null, namedSQL);
	}
	
	public static Result getResult(Connection conn, NamedSQL namedSQL) throws Exception {
		Result result = new Result();
		ReturnType returnType = namedSQL.getReturnType();
		
		if (ReturnType.EntitySet == returnType) {
			EntitySet value = getEntitySet(conn, namedSQL);
			result.setValue(value);
		}
		else if (ReturnType.Entity == returnType) {
			Entity value = getEntity(conn, namedSQL);
			result.setValue(value);
		}
		else if (ReturnType.Int == returnType) {
			int value = getInteger(conn, namedSQL);
			result.setValue(value);
		}
		else if (ReturnType.String == returnType) {
			String value = getString(conn, namedSQL, 1);
			result.setValue(value);
		}
		else if (ReturnType.BigDecimal == returnType) {
			BigDecimal value = getBigDecimal(conn, namedSQL);
			result.setValue(value);
		}		
		else if (ReturnType.ChangedCount == returnType) {
			int value = execSQL(conn, namedSQL);
			result.setValue(value);
		}
		else {
			execSQL(conn, namedSQL);
		}
		
		return result;		
	}

	public static Result getResult(NamedSQLSet namedSQLSet) throws Exception {
		Result result = null;
		
		Connection conn = SqlSession.createConnection();
		try {
			conn.setAutoCommit(false);
			
			List<NamedSQL> sqlList = namedSQLSet.getItems();
			int max = sqlList.size() - 1;
			NamedSQL namedSQL;
			
			for (int i = 0; i <= max; i++) {
				namedSQL = sqlList.get(i);
				execSQL(conn, namedSQL);
			}
			
			namedSQL = sqlList.get(max);
			result = getResult(conn, namedSQL);
			conn.commit();
		} 
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		
		return result;
	}
	
	public static void getTableMetaData(IMetaDataLoader loader) throws SQLException {
		getTableMetaData(null, loader);
	}
	
	public static void getTableMetaData(Connection conn, IMetaDataLoader loader) throws SQLException {
		String sql = null;
		Statement stmt = null;
		ResultSet rslt = null;
		
		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;
		
		try {
			if (conn == null) {
				conn = SqlSession.createConnection();
			}

			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			sql = "select * from " + loader.getTableName() + " where 1 <> 1";
			logger.debug(sql);
			
			rslt = stmt.executeQuery(sql);
			ResultSetMetaData result = rslt.getMetaData();
			loader.load(result);
		} 
		catch (Exception e) {
			onError(sql, e);
		} 
		finally {
			try {
				if (rslt != null) {
					rslt.close();					
				}
			} 
			catch (SQLException e) {
			} 
			finally {
				try {
					if (stmt != null) {
						stmt.close();						
					}
				} 
				catch (SQLException e) {
				} 
				finally {
					if (closeFlag && conn != null) {
						conn.close();
					}
				}
			}
		}
	}
	
	public static boolean isTableExists(String tableName) throws Exception {
		if (tableName == null) {
			return false;
		}
		
		if (tableSet.contains(tableName.toLowerCase())) {
			return true;
		}
		
		boolean result = isTableExists(null, tableName);
		
		if (result) {
			tableSet.add(tableName.toLowerCase());
		}
		
		return result;
	}
	
	public static boolean isTableExists(Connection conn, String tableName) throws Exception {
		String sql = null;
		Statement stmt = null;
		ResultSet rslt = null;
		
		if (conn == null) {
			conn = activeConn.get();
		}
		boolean closeFlag = conn == null;
		
		try {
			if (conn == null) {
				conn = SqlSession.createConnection();
			}

			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			sql = "select 1 from user_tables where table_name ='" + tableName.toUpperCase() + "'";
			rslt = stmt.executeQuery(sql);
			
			if (rslt.next()) {
				return true;
			}
			
			return false;
		} 
		catch (Exception e) {
			onError(sql, e);
			throw e;
		} 
		finally {
			try {
				if (rslt != null) {
					rslt.close();					
				}
			} 
			catch (SQLException e) {
			} 
			finally {
				try {
					if (stmt != null) {
						stmt.close();						
					}
				} 
				catch (SQLException e) {
				} 
				finally {
					if (closeFlag && conn != null) {
						conn.close();
					}
				}
			}
		}
	}
	
	protected static void onError(String sql, Exception e) {
		logger.error("executor exec error: " + e.getMessage());
	}

	public static void getPrimaryKeyFields(ILoadable loadable) throws Exception {
		NamedSQL namedSQL = getPrimaryKeyFieldSQL(null);
		getData(namedSQL, loadable);
	}
	
	public static String getPrimaryKeyField(Connection conn, String tableName) throws Exception {
		NamedSQL namedSQL = getPrimaryKeyFieldSQL(tableName);
		PrimaryKeyLoader loader  = new PrimaryKeyLoader();
		
		getData(conn, namedSQL, loader);
		
		return loader.getPrimaryKeyField();
	}
	
	private static NamedSQL getPrimaryKeyFieldSQL(String tableName) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("SELECT columns.owner AS table_schem, columns.table_name, columns.column_name");
		result.append(" FROM all_cons_columns columns, all_constraints constraints");
		result.append(" WHERE constraints.constraint_type = 'P'");
		
		result.append("   AND constraints.constraint_name = columns.constraint_name");
		result.append("   AND constraints.table_name = columns.table_name");
		result.append("   AND constraints.owner = columns.owner");
		
		if (tableName != null) {
			tableName = tableName.toUpperCase();
			result.append("   AND columns.table_name = '" + tableName + "'");
		}
		
		result.append(" ORDER BY table_name");
		
		NamedSQL namedSQL = new NamedSQL("getPrimaryKey", result.toString());
		
		return namedSQL;
	}

}
