import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Opportunity {
	public static Map<Integer, String> int2OppoType=new HashMap<Integer, String>();
	static {
		int2OppoType.put(1, "Normal");
		int2OppoType.put(2, "Ongoing");
		int2OppoType.put(3, "State Funding");
		int2OppoType.put(4, "Federal Funding");
		int2OppoType.put(5, "Foundation Grants");
		int2OppoType.put(6, "Corporate Giving");
	}
private int oppo_id;
private String oppo_name;
//private String oppo_deadline1;
//private String oppo_deadline2;
private List<OppoDeadline> oppo_deadlines;
private String deadlineStr;
private String oppo_from;
private String oppo_from_url;
private String oppo_detected_date;
private String oppo_pro_url;
private String oppo_fund_av;
private String oppo_desc;
private int oppo_type;
private String oppo_type_name;



public Opportunity(int oppo_id, String oppo_name, String oppo_from,
		String oppo_pro_url, String oppo_fund_av, String oppo_desc, int oppo_type, String oppo_type_name,String deadlines_str) {
	SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd-yyyy",Locale.US);
	this.oppo_detected_date=dateFormat.format(new Date());

	oppo_deadlines=new ArrayList<OppoDeadline>();
	this.deadlineStr=deadlines_str;
	
	this.oppo_id = oppo_id;
	this.oppo_name = oppo_name;
	this.oppo_from = oppo_from;
	this.oppo_pro_url = oppo_pro_url;
	this.oppo_fund_av = oppo_fund_av;
	this.oppo_desc = oppo_desc;
	this.oppo_type = oppo_type;
	this.oppo_type_name = oppo_type_name;
}

public Opportunity(int oppo_id,String oppo_name,String oppo_desc) {
	this.oppo_id=oppo_id;
	this.oppo_name=oppo_name;
	this.oppo_desc=oppo_desc;
}





public String getOppo_from_url() {
	return oppo_from_url;
}







public void setOppo_from_url(String oppo_from_url) {
	this.oppo_from_url = oppo_from_url;
}







public String getDeadlineStr() {
	return deadlineStr;
}



public void setDeadlineStr(String deadlineStr) {
	this.deadlineStr = deadlineStr;
}



public List<OppoDeadline> getOppo_deadlines() {
	return oppo_deadlines;
}



public void setOppo_deadlines(List<OppoDeadline> oppo_deadlines) {
	this.oppo_deadlines = oppo_deadlines;
}



public int getOppo_id() {
	return oppo_id;
}
public void setOppo_id(int oppo_id) {
	this.oppo_id = oppo_id;
}
public String getOppo_name() {
	return oppo_name;
}
public void setOppo_name(String oppo_name) {
	this.oppo_name = oppo_name;
}

public String getOppo_from() {
	return oppo_from;
}
public void setOppo_from(String oppo_from) {
	this.oppo_from = oppo_from;
}
public String getOppo_detected_date() {
	return oppo_detected_date;
}
public void setOppo_detected_date(String oppo_detected_date) {
	this.oppo_detected_date = oppo_detected_date;
}
public String getOppo_pro_url() {
	return oppo_pro_url;
}
public void setOppo_pro_url(String oppo_pro_url) {
	this.oppo_pro_url = oppo_pro_url;
}
public String getOppo_fund_av() {
	return oppo_fund_av;
}
public void setOppo_fund_av(String oppo_fund_av) {
	this.oppo_fund_av = oppo_fund_av;
}
public String getOppo_desc() {
	return oppo_desc;
}
public void setOppo_desc(String oppo_desc) {
	this.oppo_desc = oppo_desc;
}
public int getOppo_type() {
	return oppo_type;
}
public void setOppo_type(int oppo_type) {
	this.oppo_type = oppo_type;
}
public String getOppo_type_name() {
	return oppo_type_name;
}
public void setOppo_type_name(String oppo_type_name) {
	this.oppo_type_name = oppo_type_name;
}


}
