import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.alibaba.fastjson.JSON;

public class GrantStationExtracter implements OpportunityExtracter{
	private WebDriver d;
	private List<GrantStationOppo> oppos;
	private Map<String, String> cookieMap=new HashMap<String,String>();
	 public GrantStationExtracter(String userName,String password) {
		 	oppos=new ArrayList<GrantStationOppo>();
			System.setProperty("webdriver.chrome.driver", "D:\\GRANT_MASTER\\chromedriver_win32\\chromedriver.exe");
			d=new ChromeDriver();
			d.get("https://grantstation.com/user/login");
			WebElement userNameInput= d.findElement(By.cssSelector("input[id=edit-name]"));
			WebElement passwordInput=d.findElement(By.cssSelector("input[id=edit-pass]"));
			userNameInput.sendKeys(userName);
			passwordInput.sendKeys(password);
			d.findElement(By.cssSelector("button[id=edit-submit]")).click();
			//get cookies
			Set<Cookie> cookies= d.manage().getCookies();
			cookies.forEach(e->{
				cookieMap.put(e.getName(), e.getValue());
				
			});
			d.close();
	}
	
	
	
	@Override
	public List<? extends Opportunity> extract() {
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM/dd/yyyy");
		Date now=new Date();

		try {
			//========================================get federal info
			int page=0;
			boolean done=false;
			while(!done) {
				System.out.println("Page:"+page);
				Document d= Jsoup.connect("https://grantstation.com/search/us-federal?keywords=&opp_number=&cfda=&items_per_page=100&page="+page).
						cookies(cookieMap).
						userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
						Element table=d.select("table[class=table table-hover table-striped]").first();
						Elements trs= table.select("tr");
						//remove title
						trs.remove(0);
						//loop trs
						for(Element tr:trs) {
							Element oppoTitle_a=tr.child(0).select("a").first();
							String oppoTitleLink="https://grantstation.com"+oppoTitle_a.attr("href");
							String oppoTitleText=oppoTitle_a.text();
							String agency=tr.child(1).text();
							String postDate=tr.child(2).text();
							String closeDate=tr.child(3).text();
							//if the info is expired
							
							if(closeDate==null||closeDate.trim().equals("")) {
								closeDate="Ongoing";
							}else {
								Date closeDateD=dateFormat.parse(closeDate);
								if(now.after(closeDateD)) {
									continue;
								}
							}
							
							
							
							//get detail page
							try {
							Document detailDoc=Jsoup.connect(oppoTitleLink).
									cookies(cookieMap).
									userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
							//get desc
							String desc="";
							if(detailDoc.select("div[class=field field--name-field-description field--type-text-long field--label-hidden field--item]").size()>0) {
								desc=detailDoc.select("div[class=field field--name-field-description field--type-text-long field--label-hidden field--item]").first().text();
							}
								

							//find FINANCIAL info
							String avFund="";
							if(detailDoc.select("div[class=field field--name-field-estimated-total field--type-integer field--label-hidden field--item]").size()>0) {//finanical info existing
								avFund=detailDoc.select("div[class=field field--name-field-estimated-total field--type-integer field--label-hidden field--item]").first().text();
							}
							//get link
							String pro_link="";
							if(detailDoc.select("div[class=field field--name-field-additional-information field--type-link field--label-hidden field--item] a").size()>0) {
								pro_link=detailDoc.select("div[class=field field--name-field-additional-information field--type-link field--label-hidden field--item] a").attr("href");
							}
							if(pro_link==null||pro_link.trim().equals("")) {//find link button
								if(detailDoc.select("div[class=visit-website-link] a").size()>0) {//<a> existing
									pro_link=detailDoc.select("div[class=visit-website-link] a").first().attr("href");
								}else {//<a> not existing
									pro_link="https://grantstation.com/search/us-federal";
								}
								
							}
							
							//create new object
							GrantStationOppo oppo=new GrantStationOppo(0, oppoTitleText+" ("+agency+")", pro_link, avFund, desc, 4, "", closeDate,postDate);
							oppo.setOppo_from_url(oppoTitleLink);
							oppos.add(oppo);
							}catch (SocketException e) {
								System.out.println("Socket Time Out!!!");
							}
							
							
						}
						System.out.println("OPPO size: "+oppos.size());
						
							insertDB();
							oppos=new ArrayList<GrantStationOppo>();
						
						if(trs.size()<100) {
							done=true;
						}
						page++;
						
						
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return null;
	}
	
	private void insertDB() {
		//insert into database
		Connection con=ConnectionBuilder.getConnection();
		String querySql="select oppo_id from opportunity where oppo_name=? and oppo_pro_url=? and oppo_fund_av=?";
		String inertSql="insert into opportunity(oppo_name,oppo_from,oppo_detected_date,oppo_pro_url,"
				+ "oppo_fund_av,oppo_desc,oppo_type,oppo_deadlines,oppo_from_url,oppo_post_date,oppo_geo_scope,oppo_geo_focus) values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			
			for(GrantStationOppo o:oppos) {
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
					sta.setString(10, o.getPostDate());
					sta.setString(11, o.getGeo_scope());
					sta.setString(12, o.getGeo_focus());
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
	
	
	public void getCharitableInfo() {
		try {
			//========================================get charitable info
			int page=0;
			boolean done=false;
			while(!done) {
				System.out.println("Page:"+page);
				Document d= Jsoup.connect("https://grantstation.com/search/us-funders?grantmaker_name=&ein=&keyword=&items_per_page=100&page="+page).
						cookies(cookieMap).
						userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
						Element table=d.select("table[class=table table-hover table-striped]").first();
						Elements trs= table.select("tr");
						//remove title
						trs.remove(0);
						//loop trs
						for(Element tr:trs) {
							//System.out.println(tr.html());
							Element oppoTitle_a=tr.child(1).select("a").first();
							String oppoTitleLink="https://grantstation.com"+oppoTitle_a.attr("href");
							String oppoTitleText=oppoTitle_a.text();
							String geo_scope=tr.child(2).text();
							String geo_focus=tr.child(3).text();
							//get detail page
							try {
							Document detailDoc=Jsoup.connect(oppoTitleLink).
									cookies(cookieMap).
									userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
							//get desc
							String desc="";
							if(detailDoc.select("div[class=field field--name-field-areas-of-interest field--type-text-long field--label-hidden field--item]").size()>0) {
								desc=detailDoc.select("div[class=field field--name-field-areas-of-interest field--type-text-long field--label-hidden field--item]").first().text();
								//System.out.println(descDiv.text());
								
							}
								

							//find FINANCIAL info
							String avFund="";
							if(detailDoc.select("div[class=field field--name-field-total-annual-giving field--type-string field--label-hidden field--item]").size()>0) {//finanical info existing
								avFund=detailDoc.select("div[class=field field--name-field-total-annual-giving field--type-string field--label-hidden field--item]").first().text();
								//System.out.println(avFund);
							}
							
							
							//get link
							String pro_link="";
							if(detailDoc.select("div[field field--name-field-grantmaker-listings field--type-link field--label-hidden field--item] a").size()>0) {
								pro_link=detailDoc.select("div[class=field field--name-field-grantmaker-listings field--type-link field--label-hidden field--item] a").attr("href");
							}
							if(pro_link==null||pro_link.trim().equals("")) {//find link button
								if(detailDoc.select("div[class=visit-website-link] a").size()>0) {//<a> existing
									pro_link=detailDoc.select("div[class=visit-website-link] a").first().attr("href");
								}else {//<a> not existing
									pro_link="https://grantstation.com/search/us-funders";
								}
								
							}
							//get update date
							String postDate="";
							if(detailDoc.select("div[class=field field--name-field-lastupdated field--type-datetime field--label-hidden field--item]").size()>0) {
								postDate=detailDoc.select("div[class=field field--name-field-lastupdated field--type-datetime field--label-hidden field--item]").first().text();
							}
							
							
							//create new object
							GrantStationOppo oppo=new GrantStationOppo(0, oppoTitleText, pro_link, avFund, desc, 7, "", "",postDate);
							oppo.setOppo_from_url(oppoTitleLink);
							oppo.setGeo_scope(geo_scope);
							oppo.setGeo_focus(geo_focus);
							oppos.add(oppo);
							}catch (SocketException e) {
								System.out.println("Socket Time Out!!!");
							}
							
							
						}
						System.out.println("OPPO size: "+oppos.size());
						
							insertDB();
							oppos=new ArrayList<GrantStationOppo>();
						
						if(trs.size()<100) {
							done=true;
						}
						page++;
						
						
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		
	
	}
	
	
	public static void main(String[] args) {
		GrantStationExtracter o=new GrantStationExtracter("astewart@goodwillhunting.org", "Goodw1ll!");
		//o.extract();
		o.getCharitableInfo();
//		String d="04/25/2018";
//		SimpleDateFormat s=new SimpleDateFormat("MM/dd/yy");
//		try {
//			Date date= s.parse(d);
//			System.out.println(date.after(new Date()));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//System.out.println(s.format(new Date()));
	}

}
