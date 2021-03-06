package mvise.bosch.wdw.registrationHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class VariantService {

    private static final int BASIC_AUTH_PREFIX_LENGTH = 6;

    @Autowired
    private ConfigProperties configProperties;

    boolean isInWhitelist(String variantId) {
        return configProperties.getVariantWhitelist().stream().anyMatch(
                variant -> variant.getVariantId().equals(variantId));
    }

    /**
     * Remove Basic auth prefix,
     * decode from Base64 into byte,
     * encode using String constructor
     * and return substring to first : (The username)
     *
     * @param authorization the base64 encoded authorization header
     * @return the username decoded from authorization header
     */
    String getIdFromAuth(String authorization) {
        byte[] decodedAuth = Base64.getUrlDecoder().decode(
                authorization.substring(BASIC_AUTH_PREFIX_LENGTH, authorization.length()));
        String authString = new String(decodedAuth);

        return authString.substring(0, authString.indexOf(':'));
    }

    void setConfigProperties(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }
}
