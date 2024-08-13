package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.RoleRequest;
import nserve.delivery_application_backend.dto.response.RoleResponse;
import nserve.delivery_application_backend.mapper.RoleMapper;
import nserve.delivery_application_backend.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse create (RoleRequest request){
        var role = roleMapper.toRole(request);


        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles(){
        var roles = roleRepository.findAll();

        return roles.stream().map(roleMapper::toRoleResponse).toList();

    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }
}
