package kr.ezen.jung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import kr.ezen.jung.controller.RssReader;

@SpringBootApplication
public class JungBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(JungBlogApplication.class, args);
	}
	
	@Autowired
	private RssReader rssReader;
	
	// @Bean
	CommandLineRunner getCommandLineRunner() {
		return args -> {
			while(true) {
				rssReader.checkForUpdates();
				Thread.sleep(10000);
			}
		};
	}
}
