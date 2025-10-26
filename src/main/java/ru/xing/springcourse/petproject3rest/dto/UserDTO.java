package ru.xing.springcourse.petproject3rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User information")
public class UserDTO {
    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "User roles", example = "ADMIN_ROLE")
    private List<String> role;
}
