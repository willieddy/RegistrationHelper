package mvise.bosch.wdw.registrationHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class VariantServiceTest {

    private static final String AUTH_BASE64 = "Basic VGVzdFVzZXI6VGVzdFBhc3N3b3Jk";
    private static final String AUTH_USERNAME = "TestUser";

    private static final String FCM_API_KEY = "TestApiKey";
    private static final String VARIANT_ID = "8an2338am";
    private static final String VARIANT_ID_2 = "64fasdo13";


    private VariantService variantService;

    @Before
    public void init() {
        Variant variant = new Variant();
        variant.setFcmApiKey(FCM_API_KEY);
        variant.setVariantId(VARIANT_ID);

        List<Variant> variantList = new ArrayList<>();
        variantList.add(variant);

        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setVariantWhitelist(variantList);

        variantService = new VariantService();
        variantService.setConfigProperties(configProperties);
    }

    @Test
    public void testGetIdFromAuth() {
        String result = variantService.getIdFromAuth(AUTH_BASE64);
        Assert.assertEquals("ID decoded from base64 does not match expected", AUTH_USERNAME, result);
    }

    @Test
    public void testIsInWhitelist() {
        Assert.assertTrue(variantService.isInWhitelist(VARIANT_ID));
    }

    @Test
    public void testIsInWhitelistFalse() {
        Assert.assertFalse(variantService.isInWhitelist(VARIANT_ID_2));
    }
}
