import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpportunitiesDataReader {
	private Connection con;
	private int rowCount;
	public OpportunitiesDataReader(Connection con) {
		if(con==null) {
			throw new NullPointerException("The connection could not be null");
		}
		this.con=con;
		rowCount=0;
	}
	
	
	
	public List<Opportunity> readData2File(String filePath) {
		rowCount=0;
		String sql="select * from opportunity";
		List<Opportunity> oppos=new ArrayList<Opportunity>();
		PreparedStatement sta;
		try {
			
			File f=new File(filePath);
			FileWriter writer=new FileWriter(f);
			sta = con.prepareStatement(sql);
			ResultSet r= sta.executeQuery();
			while(r.next()) {
				Opportunity opportunity=new Opportunity(r.getInt("oppo_id"), r.getString("oppo_name"), r.getString("oppo_desc"));
				oppos.add(opportunity);
				writer.write(r.getString("oppo_name")+" "+r.getString("oppo_desc")+"\n");
				writer.flush();
				rowCount++;
			}
			
			
			writer.close();
			
		}catch (Exception e) {
			
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return oppos;
		
		
	}
	public int getRowCount() {
		return rowCount;
	}
	public static void main(String[] args) {
		
		OpportunitiesDataReader oppoReader=new OpportunitiesDataReader(ConnectionBuilder.getConnection());
		oppoReader.readData2File("C:\\Users\\l1876\\Desktop\\project_files\\oppos.txt");
		
		
	}
}
