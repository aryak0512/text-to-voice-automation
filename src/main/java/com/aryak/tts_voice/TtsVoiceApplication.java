package com.aryak.tts_voice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TtsVoiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TtsVoiceApplication.class, args);
	}

	private final StartupConfigurer configurer;

	public TtsVoiceApplication(StartupConfigurer configurer) {
		this.configurer = configurer;
	}

	@Override
	public void run(String... args) throws Exception {
		configurer.load();
	}
}
