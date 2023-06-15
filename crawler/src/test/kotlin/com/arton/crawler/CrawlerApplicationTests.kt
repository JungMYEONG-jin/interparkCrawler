package com.arton.crawler

import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CrawlerApplicationTests {

	@Autowired
	private lateinit var crawlerService: CrawlerService
	@Value("\${driver.google.path}")
	private lateinit var drivrPath: String
	@Test
	fun contextLoads() {
		println("drivrPath = ${drivrPath}")
	}

	@Test
	fun addTest() {
		val body = "{\"title\":\"싸이흠뻑쇼 SUMMERSWAG2023 - 서울\",\"link\": \"https://tickets.interpark.com/goods/23007049\",\"musicalDateTime\":\"2023.06.30~2023.07.02\",\"startDate\":\"2023.06.30\",\"endDate\":\"2023.07.02\",\"ticketOpenDate\":\"\",\"ticketEndDate\":\"\",\"place\":\"잠실 종합운동장 올림픽주경기장\",\"runningTime\":\"0\",\"limitAge\":\"0\",\"description\":\"https://ticketimage.interpark.com/Play/image/etc/23/23007049-06.jpg\",\"imageUrl\":\"https://ticketimage.interpark.com/Play/image/large/23/23007049_p.gif\",\"performanceType\":\"콘서트\",\"artists\":[{\"name\":\"싸이\",\"profileImageUrl\":\"https://ticketimage.interpark.com/PlayDictionary/DATA/PlayDic/PlayDicUpload/040004/08/01/0400040801_5044_021006.gif\",\"age\":\"0\",\"snsId\":\"\"}],\"grades\":[{\"gradeName\":\"스탠딩SR\",\"price\":\"165000\"},{\"gradeName\":\"스탠딩R\",\"price\":\"154000\"},{\"gradeName\":\"지정석SR\",\"price\":\"165000\"},{\"gradeName\":\"지정석R\",\"price\":\"154000\"},{\"gradeName\":\"지정석S\",\"price\":\"132000\"}]}";
		crawlerService.addPerformance("http://aws.hancy.kr:8333/performance/crawler", body)
	}

	@Test
	fun crawlerTest() {
//		val baseUrl: String = "https://tickets.interpark.com/goods/23007049"
//		val baseUrl: String = "https://tickets.interpark.com/goods/23007654"
//		val baseUrl: String = "https://tickets.interpark.com/goods/23005708"
		val info = crawlerService.getInfo("뮤지컬", "https://tickets.interpark.com/goods/23005374")
		println("info = ${info}")


		val webDriverID = "webdriver.chrome.driver"
		val webDriverPath = "/home/godcoder/interpark/crawler/src/main/resources/static/chromedriver"
//		val webDriverPath = "/Users/a60156077/interpark/interparkCrawler/crawler/src/main/resources/static/chromedriver"
		System.setProperty(webDriverID, webDriverPath)

		// chrome option
		val options: ChromeOptions = ChromeOptions()
		options.setBinary("")
		options.addArguments("--start-maximized")
		options.addArguments("--disable-popup-blocking")
		options.addArguments("--disable-default-apps")
		options.addArguments("--headless")
		// load
		val driver = ChromeDriver(options)

		val baseUrl = "https://ticket.interpark.com/TiKi/Special/TPRegionReserve.asp?ImgYn=Y&Ca=&Region=42001&RegionName=%BC%AD%BF%EF"
//		val baseUrl = "https://ticket.interpark.com/TiKi/Special/TPRegionReserve.asp?ImgYn=Y&Ca=&Region=42001&RegionName=%BC%AD%BF%EF#btn_genre_concert"
		try{
			driver.get(baseUrl)


			val gp = driver.findElement(By.xpath("//div[@class='Gp']"))
			val objs = gp.findElements(By.xpath("//div[@class='obj']"))
			println("contents = ${objs.size}")
			for (obj in objs) {
				val a = obj.findElement(By.xpath("div[@class='obj_tit']/a"))
				val name = a.getAttribute("name")
				when (name) {
					"btn_genre_musical" -> {
						val Gp = obj.findElement(By.xpath("div[@class='Gp']"))
						val contents = Gp.findElements(By.xpath("div[@class='content']"))
						// do crawling
						for (content in contents) {
							val a = content.findElement(By.xpath("dl/dd[@class='name']/a"))
							val href = a.getAttribute("href")
							// do service
							val info = crawlerService.getInfo("뮤지컬", href)

							println("info = ${info}")
						}
					}
					"btn_genre_concert" ->{
						val Gp = obj.findElement(By.xpath("div[@class='Gp']"))
						val contents = Gp.findElements(By.xpath("div[@class='content']"))
						// do crawling
						for (content in contents) {
//							val a = content.findElement(By.xpath("dl/dd[@class='name']/a"))
//							val href = a.getAttribute("href")
//							// do service
//							crawlerService.getInfo("콘서트", href)
						}
					}
				}
			}

		}catch (e: Exception){

		}finally {
		    driver.close()
		}
	}
}
