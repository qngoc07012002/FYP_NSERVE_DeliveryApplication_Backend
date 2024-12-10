package nserve.delivery_application_backend.controller;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.*;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.AuthenticationResponse;
import nserve.delivery_application_backend.dto.response.IntrospectResponse;
import nserve.delivery_application_backend.dto.response.SMSResponse;
import nserve.delivery_application_backend.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;
   
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/generateOTP")
    ApiResponse<SMSResponse> sendSMS(@RequestBody SMSRequest request) {
        var result = authenticationService.sendOTP(request.getPhoneNumber());

        return ApiResponse.<SMSResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/customer/generateOTP")
    ApiResponse<SMSResponse> sendSMSCustomer(@RequestBody SMSRequest request) {
        var result = authenticationService.sendOTPCustomer(request.getPhoneNumber());

        return ApiResponse.<SMSResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/driver/generateOTP")
    ApiResponse<SMSResponse> sendSMSDriver(@RequestBody SMSRequest request) {
        var result = authenticationService.sendOTPDriver(request.getPhoneNumber());

        return ApiResponse.<SMSResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/restaurant/generateOTP")
    ApiResponse<SMSResponse> sendSMSRestaurant(@RequestBody SMSRequest request) {
        var result = authenticationService.sendOTPRestaurant(request.getPhoneNumber());

        return ApiResponse.<SMSResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/verifyOTP")
    ApiResponse<SMSResponse> verifyOTP(@RequestBody SMSRequest request) {
        var result = authenticationService.verifyOTP(request.getPhoneNumber(), request.getOtp());
        return ApiResponse.<SMSResponse>builder()
                .result(result)
                .build();
    }




    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> logout(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }


}
