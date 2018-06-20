package mvise.bosch.wdw.registrationHelper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    private String deviceToken;

    public String getDeviceToken() {
        return deviceToken;
    }

    public RegistrationRequest setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        return this;
    }
}
