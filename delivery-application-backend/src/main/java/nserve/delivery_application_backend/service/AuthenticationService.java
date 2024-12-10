package nserve.delivery_application_backend.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.*;
import nserve.delivery_application_backend.dto.response.AuthenticationResponse;
import nserve.delivery_application_backend.dto.response.IntrospectResponse;
import nserve.delivery_application_backend.dto.response.SMSResponse;
import nserve.delivery_application_backend.entity.*;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    private static final String ACCOUNT_SID = "AC587ce38083646e33a9818d3ce6f3d6b7";
    private static final String ACCOUNT_PASSWORD = "2b51292c820f170e88c18e28283b7735";
    private static final String SERVICE_ID = "VA6a5cc2cac450c9e45350b856337ac19e";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private LocationRepository locationRepository;

    public SMSResponse sendOTP(String phoneNumber) {
        if (!userRepository.existsByPhoneNumber(phoneNumber)) throw new AppException(ErrorCode.USER_NOT_FOUND);

        Twilio.init(ACCOUNT_SID, ACCOUNT_PASSWORD);

        Verification verification = Verification.creator(
                        SERVICE_ID,
                        phoneNumber,
                        "sms")
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        SMSResponse smsResponse = new SMSResponse();
        smsResponse.setStatus(verification.getStatus());
        return smsResponse;
    }

    public String registerCustomer(RegisterCustomerRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }


        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .fullName(request.getName())
                .email(request.getEmail())
                .imgUrl(request.getImgUrl())
                .build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRepository.save(user);

        userRoleRepository.save(userRole);



        return "Customer created";
    }

    public String registerDriver(RegisterDriverRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Role role = roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .fullName(request.getName())
                .email(request.getEmail())
                .imgUrl(request.getImgUrl())
                .build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();



        user = userRepository.save(user);

        Driver driver = Driver.builder()
                .user(user)
                .licensePlate(request.getLisenceNumber())
                .status("ONLINE")
                .balance(0)
                .vehicleType("Bike")
                .build();

        driverRepository.save(driver);

        userRoleRepository.save(userRole);

        return "Driver created";
    }

    public String registerRestaurant(RegisterRestaurantRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }


        Role role = roleRepository.findByName("RESTAURANT")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();



        user = userRepository.save(user);

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        locationRepository.save(request.getLocation());

        Restaurant restaurant = Restaurant.builder()
                .owner(user)
                .restaurantName(request.getRestaurantName())
                .description(request.getDescription())
                .location(request.getLocation())
                .category(category)
                .rating(5)
                .status("ONLINE")
                .balance(0)
                .address(request.getLocation().getAddress())
                .imgUrl(request.getImgUrl())
                .build();

        restaurantRepository.save(restaurant);

        userRoleRepository.save(userRole);
        return "Restaurant created";
    }


    public SMSResponse sendOTPCustomer(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        log.info("User found {}", user);
        List<UserRole> roles = userRoleRepository.findByUser(user);

        boolean hasCustomerRole = false;
        for (UserRole role : roles) {
            if (role.getRole().getName().equals("CUSTOMER")) {
                hasCustomerRole = true;
                break;
            }
        }
        log.info("User has customer role {}", hasCustomerRole);
        if (!hasCustomerRole) {
            throw new AppException(ErrorCode.INVALID_USER_ROLE);
        }

        Twilio.init(ACCOUNT_SID, ACCOUNT_PASSWORD);

        Verification verification = Verification.creator(
                        SERVICE_ID,
                        phoneNumber,
                        "sms")
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        SMSResponse smsResponse = new SMSResponse();
        smsResponse.setStatus(verification.getStatus());
        return smsResponse;
    }

    public SMSResponse sendOTPDriver(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<UserRole> roles = userRoleRepository.findByUser(user);

        boolean hasDriverRole = false;
        for (UserRole role : roles) {
            if (role.getRole().getName().equals("DRIVER")) {
                hasDriverRole = true;
                break;
            }
        }

        if (!hasDriverRole) {
            throw new AppException(ErrorCode.INVALID_USER_ROLE);
        }

        Twilio.init(ACCOUNT_SID, ACCOUNT_PASSWORD);

        Verification verification = Verification.creator(
                        SERVICE_ID,
                        phoneNumber,
                        "sms")
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        SMSResponse smsResponse = new SMSResponse();
        smsResponse.setStatus(verification.getStatus());
        return smsResponse;
    }

    public SMSResponse sendOTPRestaurant(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<UserRole> roles = userRoleRepository.findByUser(user);

        boolean hasRestaurantRole = false;
        for (UserRole role : roles) {
            if (role.getRole().getName().equals("RESTAURANT")) {
                hasRestaurantRole = true;
                break;
            }
        }

        if (!hasRestaurantRole) {
            throw new AppException(ErrorCode.INVALID_USER_ROLE);
        }

        Twilio.init(ACCOUNT_SID, ACCOUNT_PASSWORD);

        Verification verification = Verification.creator(
                        SERVICE_ID,
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
        Twilio.init(ACCOUNT_SID, ACCOUNT_PASSWORD);

        try {
            var user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            SERVICE_ID,otp)
                    .setTo(phoneNumber)
                    .create();

            SMSResponse smsResponse = new SMSResponse();
            smsResponse.setStatus(verificationCheck.getStatus());
            if (verificationCheck.getStatus().equals("approved")) {
                var token = generateToken(user);
                smsResponse.setToken(token);
            }
            return smsResponse;

        } catch (Exception e) {
            SMSResponse smsResponse = new SMSResponse();
            smsResponse.setStatus("decline");
            return smsResponse;
        }
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } else {
            var token = generateToken(user);

            return AuthenticationResponse.builder()
                    .authenticated(true)
                    .token(token)
                    .build();
        }

    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();

        try {
            verifyToken(token, false);

        } catch (AppException e){
            return IntrospectResponse.builder()
                    .valid(false).build();
        }

        return IntrospectResponse.builder()
                .valid(true).build();

    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();


            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            log.info("Expiry Time: {}", expiryTime);

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e){
            log.info("Token already expired");
        }

    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var userId  = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();


    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!verified && expiryTime.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("qngoc07012002") // ten du an
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli(
                ))) // 1h
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {


            throw new RuntimeException(e);
        }

    }

    private String buildScope(User user){
        List<UserRole> roles = userRoleRepository.findByUser(user);

        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> {
                stringJoiner.add("ROLE_" + role.getRole().getName());
            });
        }

        return stringJoiner.toString();
    }



}
