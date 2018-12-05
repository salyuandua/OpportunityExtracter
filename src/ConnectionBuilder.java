

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.Stack;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * A database connection until who can provides a {@link Connection} from c3p0 connection pool.
 * @author Xuejian Li
 *
 */
public class ConnectionBuilder {
private static DataSource ds;
static{
	ds=new ComboPooledDataSource();
	
}
public static DataSource getDataSource(){
	return ds;
}
/**
 * get a connection from connection pool
 * @return a {@link Connection} instance
 */
public static Connection getConnection(){
	try {
		return ds.getConnection();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}
	
	
	public static void main(String[] args) {//test connection
		System.out.println(ConnectionBuilder.getConnection());

	}
}
