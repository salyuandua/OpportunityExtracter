import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class OpportunityService {

public List<Opportunity> getPotentialOppos(){
	String sql="SELECT opportunity.*,oppo_type.oppo_type_name FROM opportunity left join "
	+ "oppo_type on opportunity.oppo_type=oppo_type.oppo_type_id where oppo_isdislike=0 and oppo_islike=0 order by oppo_detected_date desc"; 
	return getOppos(sql);
}
	
	
	/**
	 * Get opportunities that user dislikes
	 * @return
	 */
public List<Opportunity> getDislikeOppos(){
	String sql="SELECT opportunity.*,oppo_type.oppo_type_name FROM opportunity left join "
	+ "oppo_type on opportunity.oppo_type=oppo_type.oppo_type_id where oppo_isdislike=1 order by oppo_detected_date desc"; 
	return getOppos(sql);
}
	
	
	
	/**
	 * get opportunities that user likes
	 * @return
	 */
public List<Opportunity> getLikeOppos(){
	String sql="SELECT opportunity.*,oppo_type.oppo_type_name FROM opportunity left join "
	+ "oppo_type on opportunity.oppo_type=oppo_type.oppo_type_id where oppo_islike=1 order by oppo_detected_date desc"; 
	return getOppos(sql);
	
	
}
	
	
	
	/**
	 * get opportunity list according to sql
	 * @param sql
	 * @return
	 */
public List<Opportunity> getOppos(String sql){
//	String sql="SELECT opportunity.*,oppo_type.oppo_type_name FROM opportunity left join "
//			+ "oppo_type on opportunity.oppo_type=oppo_type.oppo_type_id where oppo_type=4 order by oppo_detected_date desc"; 
	List<Opportunity> oppos=new ArrayList<Opportunity>();
	Connection con=ConnectionBuilder.getConnection();
	try {
		PreparedStatement sta=con.prepareStatement(sql);
		ResultSet rs=sta.executeQuery();
		while(rs.next()) {
			Opportunity oppo=new Opportunity(rs.getInt("oppo_id"), rs.getString("oppo_name"),rs.getString("oppo_deadlines"), rs.getString("oppo_from"),rs.getString("oppo_from_url"), rs.getString("oppo_pro_url"),
					rs.getString("oppo_fund_av"), rs.getString("oppo_desc"), rs.getInt("oppo_type"), rs.getString("oppo_type_name"),
					rs.getInt("oppo_islike"),rs.getInt("oppo_isdislike"),rs.getString("oppo_topic"));
			oppos.add(oppo);
			
			
		}
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	return oppos;
}
	
	
}
