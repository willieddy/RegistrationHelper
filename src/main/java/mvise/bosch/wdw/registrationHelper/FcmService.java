package mvise.bosch.wdw.registrationHelper;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FcmService {

    private final RestTemplate restTemplate;

    public FcmService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Object registerTopic(String deviceToken, String topicName) {
        // TODO Validate doesn't already exist? What is behavior if it does?
        return this.restTemplate.postForObject(
                "/iid/v1/{deviceToken}/rel/topics/{topicName}", new Object(), Object.class, deviceToken, topicName);
    }
}
