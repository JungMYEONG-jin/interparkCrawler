package com.arton.crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrawlerApplication

fun main(args: Array<String>) {
	runApplication<CrawlerApplication>(*args)
}
