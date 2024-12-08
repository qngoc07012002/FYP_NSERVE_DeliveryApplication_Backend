package nserve.delivery_application_backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Driver.DriverLocationUpdateRequest;
import nserve.delivery_application_backend.dto.request.Driver.DriverStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.response.DriverResponse;
import nserve.delivery_application_backend.dto.response.Restaurant.RestaurantResponse;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.Driver;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.enums.Role;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.mapper.UserMapper;
import nserve.delivery_application_backend.repository.DriverRepository;
import nserve.delivery_application_backend.repository.LocationRepository;
import nserve.delivery_application_backend.repository.RoleRepository;
import nserve.delivery_application_backend.repository.UserRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class DriverService {
    DriverRepository driverRepository;
    UserRepository userRepository;
    LocationRepository locationRepository;

    public DriverResponse getDriver() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));

        DriverResponse response = DriverResponse.builder()
                .driverId(driver.getId())
                .userId(driver.getUser().getId())
                .driverName(driver.getUser().getFullName())
                .imgUrl(driver.getUser().getImgUrl())
                .status(driver.getStatus())
                .balance(driver.getBalance())
                .build();
        return response;
    }

    public DriverResponse updateDriverStatus(String status) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));

        driver.setStatus(status);

        driverRepository.save(driver);

        return DriverResponse.builder()
                .driverId(driver.getId())
                .userId(driver.getUser().getId())
                .driverName(driver.getUser().getFullName())
                .imgUrl(driver.getUser().getImgUrl())
                .status(driver.getStatus())
                .balance(driver.getBalance())
                .build();
    }

    public void updateDriverLocation(DriverLocationUpdateRequest driverLocationUpdateRequest) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));

        driver.getLocation().setLatitude(driverLocationUpdateRequest.getLatitude());
        driver.getLocation().setLongitude(driverLocationUpdateRequest.getLongitude());

        driverRepository.save(driver);
    }
}
