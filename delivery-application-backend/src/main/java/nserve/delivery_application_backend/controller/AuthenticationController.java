package nserve.delivery_application_backend.controller;

import com.nimbusds.jose.JOSEException;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.*;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.AuthenticationResponse;
import nserve.delivery_application_backend.dto.response.IntrospectResponse;
import nserve.delivery_application_backend.dto.response.SMSResponse;
import nserve.delivery_application_backend.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;

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
    ResponseEntity<String> sendSMS(@RequestBody SMSRequest request) {

        Twilio.init("AC587ce38083646e33a9818d3ce6f3d6b7", "2b51292c820f170e88c18e28283b7735");

        Verification verification = Verification.creator(
                        "VA6a5cc2cac450c9e45350b856337ac19e", // this is your verification sid
                        request.getPhoneNumber(),
                        "sms")
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
    }

    @PostMapping("/verifyOTP")
    ResponseEntity<String> verifyOTP(@RequestBody SMSRequest request) {

        Twilio.init("AC587ce38083646e33a9818d3ce6f3d6b7", "2b51292c820f170e88c18e28283b7735");

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            "VA6a5cc2cac450c9e45350b856337ac19e", request.getOtp()) // pass verification SID here
                    .setTo(request.getPhoneNumber())// pass generated OTP here
                    .create();

            if ("approved".equals(verificationCheck.getStatus())) {
                return new ResponseEntity<>("Verification successful", HttpStatus.OK);
            }
            System.out.println(verificationCheck.getStatus());
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }

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
