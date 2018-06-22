package mvise.bosch.wdw.registrationHelper;

import com.netflix.zuul.context.RequestContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

public class TopicFilterTest {

    private static final String AUTH_KEY = "Authorization";
    private static final String AUTH_VALUE = "Basic asdfasdf";

    private static final String VARIANT_ID = "askl2898h1";
    private static final String TOKEN_ID = "h093f09nh1f";
    private static final String TOKEN_ID_COLON = "h093f0:9nh1f";

    private static final String POST_REQUEST_URI = "https://localhost/ag-push/rest/registry/device";
    private static final String DELETE_REQUEST_URI = POST_REQUEST_URI + "/" + TOKEN_ID_COLON;

    private VariantService variantService = mock(VariantService.class);
    private FcmService fcmService = mock(FcmService.class);

    private RequestContext context;
    private TopicFilter topicFilter;

    @Before
    public void init() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        context = new RequestContext();
        context.setResponse(mockHttpServletResponse);

        RequestContext.testSetCurrentContext(context);

        topicFilter = new TopicFilter();
        topicFilter.setFcmService(fcmService);
        topicFilter.setVariantService(variantService);
    }

    @After
    public void reset() {
        RequestContext.testSetCurrentContext(null);
    }

    @Test
    public void testShouldFilterOK() {
        context.getResponse().setStatus(HttpStatus.OK.value());

        Assert.assertTrue(topicFilter.shouldFilter());
    }

    @Test
    public void testShouldFilterNoContent() {
        context.getResponse().setStatus(HttpStatus.NO_CONTENT.value());

        Assert.assertTrue(topicFilter.shouldFilter());
    }

    @Test
    public void testShouldFilterNotOkOrNoContent() {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (!HttpStatus.OK.equals(httpStatus) && !HttpStatus.NO_CONTENT.equals(httpStatus)) {
                context.getResponse().setStatus(httpStatus.value());
                Assert.assertFalse(String.format("TopicFilter ran with HttpStatus %s", httpStatus),
                        topicFilter.shouldFilter());
            }
        }
    }

    @Test
    public void testRunPost() {
        createRequest(HttpMethod.POST, TOKEN_ID_COLON);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService).registerToken(TOKEN_ID_COLON, VARIANT_ID);
    }

    @Test
    public void testRunPostNoColon() {
        createRequest(HttpMethod.POST, TOKEN_ID);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService, times(0)).registerToken(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRunDelete() {
        createRequest(HttpMethod.DELETE, TOKEN_ID_COLON);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService).deleteToken(TOKEN_ID_COLON);
    }

    @Test
    public void testRunDeleteNoColon() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpMethod.DELETE.toString(), POST_REQUEST_URI + "/" + TOKEN_ID);
        request.addHeader(AUTH_KEY, AUTH_VALUE);
        context.setRequest(request);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService, times(0)).deleteToken(Mockito.anyString());
    }

    @Test
    public void testRunDeleteUrlEndsWithSlash() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpMethod.DELETE.toString(), DELETE_REQUEST_URI + "/");
        request.addHeader(AUTH_KEY, AUTH_VALUE);
        context.setRequest(request);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService).deleteToken(TOKEN_ID_COLON);
    }

    @Test
    public void testRunDeleteUrlHasParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpMethod.DELETE.toString(), DELETE_REQUEST_URI + "?breaksCode=false");
        request.addHeader(AUTH_KEY, AUTH_VALUE);
        context.setRequest(request);

        when(variantService.getIdFromAuth(AUTH_VALUE)).thenReturn(VARIANT_ID);
        when(variantService.isInWhitelist(VARIANT_ID)).thenReturn(true);

        topicFilter.run();

        verify(fcmService).deleteToken(TOKEN_ID_COLON);
    }

    private void createRequest(HttpMethod method, String tokenId) {
        MockHttpServletRequest request = null;

        if (HttpMethod.POST.equals(method)) {
            request = new MockHttpServletRequest(method.toString(), POST_REQUEST_URI);
            request.setContent(String.format("{\"deviceToken\":\"%s\"}", tokenId).getBytes());
        } else if (HttpMethod.DELETE.equals(method)) {
            request = new MockHttpServletRequest(method.toString(), DELETE_REQUEST_URI);
        }

        request.addHeader(AUTH_KEY, AUTH_VALUE);

        context.setRequest(request);
    }
}
