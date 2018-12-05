import java.util.HashMap;
import java.util.Map;

public class OppoDeadline {
	
public static Map<Integer, String> int2strMapping=new HashMap<Integer, String>();	
//public static Map<Integer, String> int2=new HashMap<Integer, String>();	
static {
	int2strMapping.put(0, "First Deadline");
	int2strMapping.put(1, "Second Deadline");
	int2strMapping.put(2, "Third Deadline");
	int2strMapping.put(3, "Fourth Deadline");
}
private int oppo_deadline_id;
private String oppo_deadline_date;
private String oppo_deadline_name;



public OppoDeadline(int oppo_deadline_id, String oppo_deadline_date, String oppo_deadline_name) {

	this.oppo_deadline_id = oppo_deadline_id;
	this.oppo_deadline_date = oppo_deadline_date;
	this.oppo_deadline_name = oppo_deadline_name;
}
public int getOppo_deadline_id() {
	return oppo_deadline_id;
}
public void setOppo_deadline_id(int oppo_deadline_id) {
	this.oppo_deadline_id = oppo_deadline_id;
}
public String getOppo_deadline_date() {
	return oppo_deadline_date;
}
public void setOppo_deadline_date(String oppo_deadline_date) {
	this.oppo_deadline_date = oppo_deadline_date;
}
public String getOppo_deadline_name() {
	return oppo_deadline_name;
}
public void setOppo_deadline_name(String oppo_deadline_name) {
	this.oppo_deadline_name = oppo_deadline_name;
}

}
