import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SelenTest extends TimerTask{
	
public  void parsePage() {
	WebDriver d=null;
	
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
		//System.out.println("ul size is "+ulList.size());
		//loop ul list
		for(int i=0;i<ulList.size();i++) {
			WebElement ul=ulList.get(i);
			List<WebElement> liList=ul.findElements(By.cssSelector("li"));
			for(WebElement li:liList) {
				Map<String, String> m=parseLi(li);
				System.out.println(m.toString());
				
			}
			
		}
		
	}catch(Exception e) {
		e.printStackTrace();
		
		//return;
	}finally {
		d.close();
	}

}
	
public static void main(String[] args) {
	
//	Timer t=new Timer();
//	t.schedule(new SelenTest(), buildDate(),1000*40);
//	//t.schedule(task, firstTime, period);
//	System.out.println("dsda");
//	System.out.println("dsadasdsa");
	new SelenTest().parsePage();
}

public static Date buildDate() {
	Calendar c=Calendar.getInstance();
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 16);
	c.set(Calendar.SECOND, 0);
	return c.getTime();
}

public static Map<String, String> parseLi(WebElement li) {
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
	Map<String, String> m=new HashMap<String,String>();
	m.put("li_text", liText);
	m.put("a_href", a_href);
	m.put("a_text", a_text);
	return m;
	
}

@Override
public void run() {
	parsePage();
	
}


}
