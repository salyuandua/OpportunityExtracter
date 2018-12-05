
public class GoodWillOrgOppo extends Opportunity{

	public GoodWillOrgOppo(int oppo_id, String oppo_name,
			String oppo_from, String oppo_pro_url, String oppo_fund_av, String oppo_desc, int oppo_type,
			String oppo_type_name,String deadlines_str) {
		super(oppo_id, oppo_name,  oppo_from, oppo_pro_url, oppo_fund_av, oppo_desc, oppo_type,
				oppo_type_name,deadlines_str);
		
	}
	public GoodWillOrgOppo() {
		super(0, "", "", "","","", 0, "","");
	}
	
	
	

}
