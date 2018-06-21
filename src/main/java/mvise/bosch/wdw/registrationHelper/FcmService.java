package mvise.bosch.wdw.registrationHelper;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class FcmService {

    private static Logger log = LoggerFactory.getLogger(FcmService.class);

    @Autowired
    private ConfigProperties configProperties;

    private final RestTemplate restTemplate;

    /**
     * API Specification for FCM requests an empty post body, information is stored in Path Variables
     * @param restTemplateBuilder
     */
    public FcmService(RestTemplateBuilder restTemplateBuilder) {
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.restTemplate = restTemplateBuilder.messageConverters(jsonHttpMessageConverter).build();
    }

    void registerToken(String deviceToken, String topicName) {
        log.info("Sending topic subscribe request for device token {} and topic name {}", deviceToken, topicName);

        try {
            this.restTemplate.postForObject(
                    configProperties.getFcmPostUrl(), new Object(), Object.class, deviceToken, topicName);
        } catch (RestClientException e) {
            log.error("Unable to register topic {} for device with token {}", topicName, deviceToken, e);
        }
    }

    void deleteToken(String deviceToken) {
        log.info("Sending topic unsubscribe request for device token {}", deviceToken);

        try {
            this.restTemplate.delete(
                    configProperties.getFcmDeleteUrl(), new Object(), Object.class, deviceToken);
        } catch (RestClientException e) {
            log.error("Unable to unregister device with token {}", deviceToken, e);
        }
    }
}
