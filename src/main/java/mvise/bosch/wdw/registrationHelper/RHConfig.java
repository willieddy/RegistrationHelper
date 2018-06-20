package mvise.bosch.wdw.registrationHelper;

import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RHConfig {

    private String fcmUrl;

    private List<Variant> variantWhitelist;

    public String getFcmUrl() {
        return fcmUrl;
    }

    public void setFcmUrl(String fcmUrl) {
        this.fcmUrl = fcmUrl;
    }

    public List<Variant> getVariantWhitelist() {
        return variantWhitelist;
    }

    public void setVariantWhitelist(List<Variant> variantWhitelist) {
        this.variantWhitelist = variantWhitelist;
    }
}
