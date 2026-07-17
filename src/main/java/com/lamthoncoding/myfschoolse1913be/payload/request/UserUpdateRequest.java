package com.lamthoncoding.myfschoolse1913be.payload.request;

import com.lamthoncoding.myfschoolse1913be.contraints.RoleName;
import com.lamthoncoding.myfschoolse1913be.contraints.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    
    private UserStatus status;
    private Set<RoleName> roles;
}
