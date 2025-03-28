package com.calendarugr.auth_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserDTO {
    
    private Long id;

    private String nickname;

    private String email;

    private String password;

    private RoleDTO role;
}
