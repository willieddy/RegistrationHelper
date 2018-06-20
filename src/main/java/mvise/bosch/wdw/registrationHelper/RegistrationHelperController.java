package mvise.bosch.wdw.registrationHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/ag-push/rest/registry/device")
public class RegistrationHelperController {

    @Autowired
    private VariantService variantService;

    @Autowired
    private FcmService fcmService;

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<RegistrationRequest> register(@RequestBody RegistrationRequest registrationRequest,
            @RequestHeader("Authorization") String authorization) {

        String variantId = variantService.getIdFromAuth(authorization);

        if (variantService.isInWhitelist(variantId) && registrationRequest.getDeviceToken().contains(":")) {
            fcmService.registerTopic(registrationRequest.getDeviceToken(), variantId);
        }

        return new ResponseEntity<>(registrationRequest, HttpStatus.OK);
    }
}
