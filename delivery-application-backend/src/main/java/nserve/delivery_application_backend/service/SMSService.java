package nserve.delivery_application_backend.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.SMSResponse;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class SMSService {
    UserRepository userRepository;
    public SMSResponse sendOTP(String phoneNumber) {
        if (!userRepository.existsByPhoneNumber(phoneNumber)) throw new AppException(ErrorCode.PHONE_INVALID);

        Twilio.init("AC587ce38083646e33a9818d3ce6f3d6b7", "2b51292c820f170e88c18e28283b7735");

        Verification verification = Verification.creator(
                        "VA6a5cc2cac450c9e45350b856337ac19e",
                        phoneNumber,
                        "sms")
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        SMSResponse smsResponse = new SMSResponse();
        smsResponse.setStatus(verification.getStatus());
        return smsResponse;
    }

    public SMSResponse verifyOTP(String phoneNumber, String otp){
        Twilio.init("AC587ce38083646e33a9818d3ce6f3d6b7", "2b51292c820f170e88c18e28283b7735");

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            "VA6a5cc2cac450c9e45350b856337ac19e",otp)
                    .setTo(phoneNumber)
                    .create();

            SMSResponse smsResponse = new SMSResponse();
            smsResponse.setStatus(verificationCheck.getStatus());
            return smsResponse;

        } catch (Exception e) {
            SMSResponse smsResponse = new SMSResponse();
            smsResponse.setStatus("decline");
            return smsResponse;
        }
    }
}
