package ru.xing.springcourse.petproject3rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.UserDTO;
import ru.xing.springcourse.petproject3rest.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "User manager endpoints (Admin only)")
public class AdminController {

    private final UserService userService;


    @Operation(
            summary = "Get all users",
            description = "Returns paginated list of all users with their roles. Requires ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    }
    )
    @GetMapping("/users")
    public Page<UserDTO> getAllUsers(
            @PageableDefault(size = 20, sort = "username", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.info("Admin: Getting all users with pagination");
        return userService.getAllUsers(pageable);
    }

    @Operation(
            summary = "Get user by username",
            description = "Returns detailed information about specific user. Requires ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @GetMapping("/users/{username}")
    public UserDTO getUserByUsername(
            @Parameter(description = "Username to retrieve", example = "user")
            @PathVariable String username) {

        log.info("Admin: Retrieving user by username {}", username);
        return userService.getUserByUsername(username);
    }

    @Operation(
            summary = "Promote user to ADMIN",
            description = "Grant ADMIN role to user. Requires ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted successfully"),
            @ApiResponse(responseCode = "400", description = "User already has ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @PostMapping("/users/{username}/promote")
    public ResponseEntity<Map<String, Object>> promoteToAdmin(
            @Parameter(description = "Username to promote", example = "user")
            @PathVariable String username) {
        UserDTO userDTO = userService.promoteToAdmin(username);

        return ResponseEntity.ok(Map.of(
                "message", "User promoted to ADMIN",
                "user", userDTO,
                "role", userDTO.getRole()
        ));
    }

    @Operation(
            summary = "Demote user from ADMIN",
            description = "Remove ADMIN role from user. Requires ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User demoted successfully"),
            @ApiResponse(responseCode = "400", description = "User doesn't have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @PostMapping("/users/{username}/demote")
    public ResponseEntity<Map<String, Object>> demoteToAdmin(
            @Parameter(description = "User to demote", example = "superadmin")
            @PathVariable String username) {
        UserDTO userDTO = userService.demoteFromAdmin(username);

        return ResponseEntity.ok(Map.of(
                "message", "User demoted from ADMIN",
                "user", userDTO,
                "role", userDTO.getRole()
        ));
    }

    @Operation(
            summary = "Update user roles",
            description = "Update user roles directly. Roles should be comma-separated (e.g., 'USER,ADMIN'). Requires ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid roles"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @PutMapping("/users/{username}/roles")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @Parameter(description = "Username", example = "user")
            @PathVariable String username,

            @Parameter(description = "Role", example = "USER")
            @RequestParam String role) {
        log.info("Admin: Updating roles for user {}: {}", username, role);

        UserDTO user = userService.updateUserRoles(username, role);

        return ResponseEntity.ok(Map.of(
                "message", "User roles updated successfully",
                "user", user.getUsername(),
                "roles", role
        ));
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user from system. Requires ADMIN role. Cannot delete yourself.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @DeleteMapping("/users/{username}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @Parameter(description = "Username to delete", example = "testuser")
            @PathVariable String username
    ) {
        log.info("Admin: Deleting user: {}", username);
        userService.deleteUser(username);

        return ResponseEntity.ok(Map.of(
                "message", "User deleted successfully",
                "username", username
        ));
    }
}
