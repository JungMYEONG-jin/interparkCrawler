package com.arton.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.servlet.function.ServerResponse.async
import java.time.Duration

@SpringBootTest
class CrawlerApplicationTests {

	@Autowired
	private lateinit var crawlerService: CrawlerService;
	@Test
	fun contextLoads() {

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
