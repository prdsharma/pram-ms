package pram.ms.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import pram.ms.userservice.kafka.KafkaConsumerRunner;

import java.util.Locale;

@SpringBootApplication
public class UserServiceApplication {

	@Value("${kafka.bootstrap-server}")
	private String kafkaBootStarpServer;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return  localeResolver;
	}

	/*@Bean
	public ApplicationRunner startKafkaConsumers() {
		ApplicationRunner applicationRunner = (ApplicationArguments applicationArguments) -> {
			KafkaConsumerRunner kafkaConsumerRunner = new KafkaConsumerRunner(kafkaBootStarpServer);
			kafkaConsumerRunner.run("first_topic","my_user_service_app");
		};
		return applicationRunner;
	}*/


}
