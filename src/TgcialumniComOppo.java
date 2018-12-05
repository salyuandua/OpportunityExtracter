
public class TgcialumniComOppo extends Opportunity{
private String oppo_homepage;
private String oppo_eli;
private String oppo_post_date;
	public TgcialumniComOppo(int oppo_id, String oppo_name, String oppo_pro_url, String oppo_fund_av,
			String oppo_desc, int oppo_type, String oppo_type_name) {
		super(oppo_id, oppo_name, "tgcialumni.com", oppo_pro_url, oppo_fund_av, oppo_desc, oppo_type, oppo_type_name,"");
		
	}
	
	
	
	
	public String getOppo_homepage() {
		return oppo_homepage;
	}
	public void setOppo_homepage(String oppo_homepage) {
		this.oppo_homepage = oppo_homepage;
	}
	public String getOppo_eli() {
		return oppo_eli;
	}
	public void setOppo_eli(String oppo_eli) {
		this.oppo_eli = oppo_eli;
	}
	public String getOppo_post_date() {
		return oppo_post_date;
	}
	public void setOppo_post_date(String oppo_post_date) {
		this.oppo_post_date = oppo_post_date;
	}
	

}
