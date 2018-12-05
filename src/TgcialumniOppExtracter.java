
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;



public class TgcialumniOppExtracter implements OpportunityExtracter{
private WebDriver drive;
private Map<String,String> cookies;
private List<TgcialumniComOppo> oppos;
public TgcialumniOppExtracter() {
	System.setProperty("webdriver.chrome.driver", "D:\\GRANT_MASTER\\chromedriver_win32\\chromedriver.exe");
	drive=new ChromeDriver();
	drive.get("https://www.tgcialumni.com/");
	drive.switchTo().frame("bFrame");
	//set user name
	drive.findElement(By.cssSelector("input[name=UserName]")).sendKeys("floor");
	//set password
	drive.findElement(By.cssSelector("input[name=PassWord]")).sendKeys("ceiling");
	//try to log in
	drive.findElement(By.cssSelector("input[name=logIn]")).click();
	cookies=new HashMap<String,String>();
	drive.manage().getCookies().forEach(e->{
		cookies.put(e.getName(), e.getValue());
		
	});
	oppos=new ArrayList<TgcialumniComOppo>();
	drive.close();
}


	@SuppressWarnings("deprecation")
	@Override
	public List<TgcialumniComOppo> extract() {
		try {
			Document d= Jsoup.connect("https://www.tgcialumni.com/pages/fedCalendar.asp?recPerpage=200&home=0").
					cookies(cookies).validateTLSCertificates(false).
					userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
			Elements trs=d.select("table[cellpadding=3]").get(1).select("tr");
			trs.remove(0);trs.remove(0);
			for(Element tr:trs) {
				String firstDeadline=tr.child(0).text();
				String secondDeadline=tr.child(1).text();
				if(firstDeadline.equals("N/A")||firstDeadline.equals("")) {
					firstDeadline="";
				}
				if(secondDeadline.equals("N/A")||secondDeadline.equals("")) {
					secondDeadline="";
				}
				String deadlines_str=firstDeadline+" "+secondDeadline;
				
				Element a=null;
				String titleLink="";
				String titleText="";
				String av_fund="";
				String pro_url="";
				String post_date="";
				String desc="";
				if((a=tr.child(2).selectFirst("a"))!=null) {
					titleLink="https://www.tgcialumni.com/pages/"+a.attr("href");
					titleText=a.text();
				}
				//==================================go to detail page
				if(!titleLink.equals("")) {
					Document detailDoc= Jsoup.connect(titleLink).
							cookies(cookies).validateTLSCertificates(false).
							userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
					Elements innerTrs=detailDoc.select("table[cellpadding=2][width=100%]").first().select("tr");
					//get Agency
					if(innerTrs.get(6).child(1)!=null) {
						titleText+="("+innerTrs.get(6).child(1).text()+")";
					}
					//get av fund
					if(innerTrs.get(8).child(1)!=null) {
						av_fund=innerTrs.get(8).child(1).text();
					}
					//get program url
					if(innerTrs.get(12).select("a").size()>0) {//<a> existing
						pro_url=innerTrs.get(12).select("a").attr("href");
						
					}
					//get posted date
					if(innerTrs.get(14).child(1)!=null) {
						post_date=innerTrs.get(14).child(1).text();
					}
					//get desc
					if(innerTrs.get(15).child(1)!=null) {
						desc=innerTrs.get(15).child(1).text();
					}
				}
				//create new Object
				TgcialumniComOppo oppo=new TgcialumniComOppo(0, titleText, pro_url, av_fund, desc, 4, "");
				oppo.setDeadlineStr(deadlines_str);
				oppo.setOppo_from_url(titleLink);
				oppo.setOppo_post_date(post_date);
				oppos.add(oppo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
public static void main(String[] args) {
	TgcialumniOppExtracter t=new TgcialumniOppExtracter();
	t.extract();
}

}
