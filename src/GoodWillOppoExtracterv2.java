import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.alibaba.fastjson.JSON;

public class GoodWillOppoExtracterv2 implements OpportunityExtracter {
	private WebDriver d;
	private List<GoodWillOrgOppo> oppo;
	public GoodWillOppoExtracterv2(String userName,String password) {
		System.setProperty("webdriver.chrome.driver", "D:\\GRANT_MASTER\\chromedriver_win32\\chromedriver.exe");
		d=new ChromeDriver();
		oppo=new ArrayList<GoodWillOrgOppo>();
		d.get("https://secure.goodwill.org/");
		WebElement subInput=d.findElement(By.id("btnSubmit"));
		d.findElement(By.id("Password")).sendKeys(password);
		d.findElement(By.id("UserId")).sendKeys(userName);
		subInput.click();
		
		
		
	}
	/**
	 * parse td
	 * @param tds
	 */
	private void parseTds(List<WebElement> tds ) {
		//funder name
		String funderName=tds.get(0).getText();
		//funder link
		WebElement a=tds.get(1).findElement(By.cssSelector("a"));
		String funderLink=a.getAttribute("href");
		String funderLinkName=a.getText();
		//funder desc
		WebElement desc_a=tds.get(2).findElement(By.cssSelector("a"));
		String descLink=desc_a.getAttribute("href");
		String desc=desc_a.getText();
		//funding deadline
		String deadline=tds.get(3).getText();
		//funding amount
		String amount=tds.get(4).getText();
		//new oppo object
		
		GoodWillOrgOppo oppo=new GoodWillOrgOppo(0, funderName, "goodwill.org", funderLink, amount, desc, 1, "Normal",deadline);
		oppo.setOppo_from_url("https://my.goodwill.org/Pages/Home.aspx");
		//System.out.println(JSON.toJSONString(oppo));
		this.oppo.add(oppo);
	}

	
	private void parseTbody() {
		//get tbody
				WebElement tbody=d.findElement(By.cssSelector("tbody[isloaded=true]"));
				//System.out.println(tbody.getAttribute("isloaded"));
				//get tr
				List<WebElement> trs=tbody.findElements(By.cssSelector("tr"));
				for(WebElement tr:trs) {
					//get td
					List<WebElement> tds=tr.findElements(By.cssSelector("td"));
					parseTds(tds);
					
					
				}
	}
	@Override
	public List<? extends Opportunity> extract() {
		//=================================get grant alert info
		d.get("https://my.goodwill.org/sites/communities/Resource-Development/Pages/Open-Grants.aspx");
		parseTbody();
		//===================================get Foundation info
		d.get("https://my.goodwill.org/sites/communities/Resource-Development/Pages/Open-Grants.aspx?gt=Foundation");
		parseTbody();
		//===================================get Corporate info
		d.get("https://my.goodwill.org/sites/communities/Resource-Development/Pages/Open-Grants.aspx?gt=Corporate");
		parseTbody();
		//====================================get govenerment
		d.get("https://my.goodwill.org/sites/communities/Resource-Development/Pages/Open-Grants.aspx?gt=Government");
		parseTbody();
		System.out.println(JSON.toJSONString(this.oppo));
		//insert into db
		insertToDb();
		d.close();
		
		
		
		
		return null;
	}
	
	
	/**
	 * //insert oppo list to db
	 */
	private void insertToDb() {
		//insert into database
		Connection con=ConnectionBuilder.getConnection();
		String querySql="select oppo_id from opportunity where oppo_name=? and oppo_pro_url=? and oppo_fund_av=?";
		String inertSql="insert into opportunity(oppo_name,oppo_from,oppo_detected_date,oppo_pro_url,"
				+ "oppo_fund_av,oppo_desc,oppo_type,oppo_deadlines,oppo_from_url) values (?,?,?,?,?,?,?,?,?)";
		
		try {
			
			for(GoodWillOrgOppo o:this.oppo) {
				//check oppo is exist or not
				PreparedStatement sta=con.prepareStatement(querySql);
				sta.setString(1, o.getOppo_name());
				sta.setString(2, o.getOppo_pro_url());
				sta.setString(3, o.getOppo_fund_av());
				ResultSet r=sta.executeQuery();
				if(!r.first()) {//not existing
					sta=con.prepareStatement(inertSql);
					sta.setString(1, o.getOppo_name());
					sta.setString(2, o.getOppo_from());
					sta.setString(3, o.getOppo_detected_date());
					sta.setString(4, o.getOppo_pro_url());
					sta.setString(5, o.getOppo_fund_av());
					sta.setString(6, o.getOppo_desc());
					sta.setInt(7, o.getOppo_type());
					sta.setString(8, o.getDeadlineStr());
					sta.setString(9, o.getOppo_from_url());
					sta.executeUpdate();
					
					
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
public static void main(String[] args) {
	OpportunityExtracter o=new GoodWillOppoExtracterv2("aturner@goodwillhunting.org", "Goodw1ll66!");
	o.extract();
}
}
