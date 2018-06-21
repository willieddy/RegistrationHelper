package mvise.bosch.wdw.registrationHelper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class ConfigProperties {

    private List<Variant> variantWhitelist;

    private String fcmPostUrl;

    private String fcmDeleteUrl;

    public List<Variant> getVariantWhitelist() {
        return variantWhitelist;
    }

    public void setVariantWhitelist(List<Variant> variantWhitelist) {
        this.variantWhitelist = variantWhitelist;
    }

    public String getFcmPostUrl() {
        return fcmPostUrl;
    }

    public void setFcmPostUrl(String fcmPostUrl) {
        this.fcmPostUrl = fcmPostUrl;
    }

    public String getFcmDeleteUrl() {
        return fcmDeleteUrl;
    }

    public void setFcmDeleteUrl(String fcmDeleteUrl) {
        this.fcmDeleteUrl = fcmDeleteUrl;
    }
}
