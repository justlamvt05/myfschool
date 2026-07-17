package com.lamthoncoding.myfschoolse1913be.service;

import com.lamthoncoding.myfschoolse1913be.dto.UserDto;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserCreateRequest;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDto getMyProfile(String phone);

    String changePassword (String newPass,String confirmPassword, String phone);

   Page<UserDto> getUsers(String name, String phone, Pageable pageable);

    UserDto createUser(UserCreateRequest request);

    UserDto updateUser(Long id, UserUpdateRequest request);

    void toggleUserStatus (Long id);

    List<UserDto> importUsers(MultipartFile file);

    byte[] exportUsers(String name, String phone);
}
