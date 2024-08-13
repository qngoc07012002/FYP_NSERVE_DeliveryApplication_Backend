package nserve.delivery_application_backend.mapper;

import nserve.delivery_application_backend.dto.request.RoleRequest;
import nserve.delivery_application_backend.dto.response.RoleResponse;
import nserve.delivery_application_backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole (RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
