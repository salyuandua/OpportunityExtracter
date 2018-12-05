import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.alibaba.fastjson.JSON;

public class GoodWillOppExtracter implements OpportunityExtracter{

	@Override
	public List<GoodWillOrgOppo> extract() {
		
		WebDriver d=null;
		List<GoodWillOrgOppo> oppoList=null;
		try {
			System.setProperty("webdriver.chrome.driver", "D:\\GRANT_MASTER\\chromedriver_win32\\chromedriver.exe");
			d=new ChromeDriver();
			d.get("https://secure.goodwill.org/");
			WebElement subInput=d.findElement(By.id("btnSubmit"));
			d.findElement(By.id("Password")).sendKeys("Goodw1ll66!");
			d.findElement(By.id("UserId")).sendKeys("aturner@goodwillhunting.org");
			subInput.click();
			d.get("https://my.goodwill.org/sites/communities/Resource-Development/Pages/Open-Grants.aspx");
			//parse d
			//get ul list
			List<WebElement> ulList=  d.findElements(By.cssSelector("ul.ms-rteThemeForeColor-2-0"));
			//each li represents an attribute of an opportunity object
			//Normal Opportunity
			WebElement normalOppoUl=ulList.get(0);
			//ongoing 
			WebElement onGoingOppoUl=ulList.get(1);
			
			oppoList=parseUl(normalOppoUl, 1);
			oppoList.addAll(parseUl(onGoingOppoUl, 2));
			
		}catch(Exception e) {
			e.printStackTrace();
			
			//return;
		}finally {
			d.close();
		}
		return oppoList;
	}
	
	/**
	 * Parse ul element, each ul contains many opportunities
	 * @param ul
	 * @param oppo_type
	 * @return
	 */
	
	private List<GoodWillOrgOppo> parseUl(WebElement ul,int oppo_type_id) {
		List<WebElement> liList=ul.findElements(By.cssSelector("li"));
		List<GoodWillOrgOppo> l=new ArrayList<GoodWillOrgOppo>(liList.size());
		for(WebElement li:liList) {//each li represents an attribute of an opportunity object
			GoodWillOrgOppo oppo=new GoodWillOrgOppo();
			oppo.setOppo_from("www.goodwill.org");
			oppo.setOppo_type(oppo_type_id);
			oppo=parseLi(li,oppo);
			l.add(oppo);
			
		}
		return l;
	}
	
	
	
	/**
	 * each li represents an attribute of an opportunity object
	 * @param li
	 * @param bean
	 * @return
	 */
	private  GoodWillOrgOppo parseLi(WebElement li,GoodWillOrgOppo bean) {
		String liText="";
		String a_href="";
		String a_text="";
		try {
			liText=li.getText();
			a_href=li.findElement(By.tagName("a")).getAttribute("href");
			a_text=li.findElement(By.tagName("a")).getText();
		}catch(NoSuchElementException e) {
			System.out.println("li has no <a>");
		}
		bean.setOppo_desc(liText);
		bean.setOppo_pro_url(a_href);
		bean.setOppo_name(a_text);
		return bean;
		
	}
	
public static void main(String[] args) {
	String text="The U.S. Department of Labor’s Veterans Employment and Training Service announces Stand Down grants to support homeless veterans. Deadline: Ongoing through 12/31/2020 ";
	GoodWillOrgOppo bean=new GoodWillOrgOppo();
	bean=parseFundAmount(bean, text);
	bean=parseDeadlines(bean, text);
	System.out.println(JSON.toJSONString(bean));
	
}


/**
 * Parsing amount of funding from description text
 * @param bean
 * @param text
 * @return
 */
private static GoodWillOrgOppo parseFundAmount(GoodWillOrgOppo bean,String text) {
	if(text.contains("$")) {//amount of funding existing
		String candidateText= text.substring(text.indexOf("$")+1, text.indexOf(" ", text.indexOf("$")));
		//System.out.println(candidateText);
		StringBuilder sb=new StringBuilder();
		for(char c:candidateText.toCharArray()) {
			if(Character.isDigit(c)||c==',') {
				sb.append(c);
			}
			
		}
		
		bean.setOppo_fund_av(sb.toString());
		
	}
	
	return bean;
	
}


/**
 * parse deadlines from description text
 * @param bean
 * @param text
 * @return
 */
private static GoodWillOrgOppo parseDeadlines(GoodWillOrgOppo bean,String text) {
	
	if(text.contains("Deadline")) {
		
		//System.out.println(text.substring(text.lastIndexOf("Deadline")+"Deadline".length()));	
		char[] textArr=text.substring(text.lastIndexOf("Deadline")+"Deadline".length()).toCharArray();
		StringBuilder newText=new StringBuilder();
		for(char c:textArr) {
			if(Character.isDigit(c)||c=='/'||c=='-') {
				newText.append(c);
			}
		}
		
		//System.out.println(newText.toString());
		String[] strArr=newText.toString().split("-");
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd-yy");
		SimpleDateFormat newDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<strArr.length;i++) {
			String date=strArr[i].replace("/", "-");
			
			try {
				Date d=dateFormat.parse(date);
				date=newDateFormat.format(d);
				bean.getOppo_deadlines().add(new OppoDeadline(0, date, OppoDeadline.int2strMapping.get(i)));
			} catch (ParseException e) {
				System.out.println("Parse date failed");
				//e.printStackTrace();
				
			}
			
			
		}
		
		
		
	}
	return bean;
	
	
}
	
	
}
