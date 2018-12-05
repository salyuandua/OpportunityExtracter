
public class GrantStationOppo extends Opportunity{
private String postDate;
private String geo_scope;
private String geo_focus;
	public GrantStationOppo(int oppo_id, String oppo_name, String oppo_pro_url, String oppo_fund_av,
			String oppo_desc, int oppo_type, String oppo_type_name, String deadlines_str,String postDate) {
		
		super(oppo_id, oppo_name, "grantstation.com", oppo_pro_url, oppo_fund_av, oppo_desc, oppo_type, oppo_type_name, deadlines_str);
		this.postDate=postDate;
		this.setOppo_from_url("https://grantstation.com/");
		this.geo_scope="";
		this.geo_focus="";
	}
	public String getPostDate() {
		return postDate;
	}
	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}
	public String getGeo_scope() {
		return geo_scope;
	}
	public void setGeo_scope(String geo_scope) {
		this.geo_scope = geo_scope;
	}
	public String getGeo_focus() {
		return geo_focus;
	}
	public void setGeo_focus(String geo_focus) {
		this.geo_focus = geo_focus;
	}
	

}
