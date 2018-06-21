package mvise.bosch.wdw.registrationHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringBootApplication
public class RegistrationHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHelperApplication.class, args);
	}

	@Bean
    public TopicFilter topicFilter() {
	    return new TopicFilter();
    }
}
