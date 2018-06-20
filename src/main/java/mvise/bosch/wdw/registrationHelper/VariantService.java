package mvise.bosch.wdw.registrationHelper;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class VariantService {

    private List<String> variantBlacklist;

    public VariantService() {
        //TODO Initialize list
    }

    boolean isInBlacklist(String variantId) {
        return variantBlacklist.contains(variantId);
    }

    String getIdFromAuth(String authorization) {
        byte[] decodedAuth = Base64.getMimeDecoder().decode(authorization);
        String authString = new String(decodedAuth);

        return authString.substring(0, authString.indexOf(':'));
    }
}
