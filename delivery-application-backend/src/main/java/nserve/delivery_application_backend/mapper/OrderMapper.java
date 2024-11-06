package nserve.delivery_application_backend.mapper;

import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

}
