package mvise.bosch.wdw.registrationHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class TopicFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(TopicFilter.class);

    @Autowired
    private VariantService variantService;

    @Autowired
    private FcmService fcmService;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 999;
    }

    @Override
    public boolean shouldFilter() {
        int responseStatus = RequestContext.getCurrentContext().getResponse().getStatus();
        return  responseStatus == HttpStatus.OK.value() || responseStatus == HttpStatus.NO_CONTENT.value();
    }

    /**
     * Determines if variant needs a topic posted to FCM based on two criteria
     * 1. The variant ID (from auth header) is in the whitelist
     * 2. The device token (from post body) contains a ':'
     *
     * The determination should occur in shouldFilter(), however this duplicates effort
     *  getting the variant ID and device token
     * @return null
     */
    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();

            String authorization = request.getHeader("Authorization");
            String variantId = variantService.getIdFromAuth(authorization);

            if (variantService.isInWhitelist(variantId)) {
                if (HttpMethod.POST.toString().equalsIgnoreCase(request.getMethod())) {
                    handlePost(request, variantId);
                } else if (HttpMethod.DELETE.toString().equalsIgnoreCase(request.getMethod())) {
                    handleDelete(request);
                }
            }
        } catch (IOException e) {
            log.error("Unable to create or delete topic", e);
        }

        return null;
    }

    private void handlePost(HttpServletRequest request, String variantId) throws IOException {
        RegistrationRequest registrationRequest = new ObjectMapper().readValue(
                request.getReader().lines().collect(Collectors.joining(System.lineSeparator())),
                RegistrationRequest.class);

        if (registrationRequest.getDeviceToken().contains(":")) {
            log.info("Setting topic {} for device token {}",
                    variantId, registrationRequest.getDeviceToken());

            fcmService.registerToken(registrationRequest.getDeviceToken(), variantId);
        }
    }

    /**
     * Retrieve deviceToken from request URI by manually clipping from last '/' to end of URI
     * Send request to FCM to unregister this token
     * @param request contains URI with deviceToken
     */
    private void handleDelete(HttpServletRequest request) {
        String deviceToken= request.getRequestURI().substring(
                request.getRequestURI().lastIndexOf('/'),
                request.getRequestURI().length());

        fcmService.deleteToken(deviceToken);
    }

    void setVariantService(VariantService variantService) {
        this.variantService = variantService;
    }

    void setFcmService(FcmService fcmService) {
        this.fcmService = fcmService;
    }
}
