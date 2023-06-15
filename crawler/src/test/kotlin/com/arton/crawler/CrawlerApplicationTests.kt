package com.arton.crawler

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
	private lateinit var driverPath: String
	@Test
	fun contextLoads() {
		println("drivrPath = ${driverPath}")
	}

	@Test
	fun addTest() {
		val body = "{\"title\":\"싸이흠뻑쇼 SUMMERSWAG2023 - 서울\",\"link\": \"https://tickets.interpark.com/goods/23007049\",\"musicalDateTime\":\"2023.06.30~2023.07.02\",\"startDate\":\"2023.06.30\",\"endDate\":\"2023.07.02\",\"ticketOpenDate\":\"\",\"ticketEndDate\":\"\",\"place\":\"잠실 종합운동장 올림픽주경기장\",\"runningTime\":\"0\",\"limitAge\":\"0\",\"description\":\"https://ticketimage.interpark.com/Play/image/etc/23/23007049-06.jpg\",\"imageUrl\":\"https://ticketimage.interpark.com/Play/image/large/23/23007049_p.gif\",\"performanceType\":\"콘서트\",\"artists\":[{\"name\":\"싸이\",\"profileImageUrl\":\"https://ticketimage.interpark.com/PlayDictionary/DATA/PlayDic/PlayDicUpload/040004/08/01/0400040801_5044_021006.gif\",\"age\":\"0\",\"snsId\":\"\"}],\"grades\":[{\"gradeName\":\"스탠딩SR\",\"price\":\"165000\"},{\"gradeName\":\"스탠딩R\",\"price\":\"154000\"},{\"gradeName\":\"지정석SR\",\"price\":\"165000\"},{\"gradeName\":\"지정석R\",\"price\":\"154000\"},{\"gradeName\":\"지정석S\",\"price\":\"132000\"}]}";
		crawlerService.addPerformance("http://aws.hancy.kr:8333/performance/crawler", body)
	}

	@Test
	fun crawlerTest() {
		runBlocking {
			crawlerService.travelInterPark()
		}
	}
}
