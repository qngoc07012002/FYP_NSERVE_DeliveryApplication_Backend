package nserve.delivery_application_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.RoleRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.RoleResponse;
import nserve.delivery_application_backend.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class RoleController {

    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
