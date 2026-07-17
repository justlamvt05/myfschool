package com.lamthoncoding.myfschoolse1913be.service.impl;

import com.lamthoncoding.myfschoolse1913be.contraints.RoleName;
import com.lamthoncoding.myfschoolse1913be.contraints.UserStatus;
import com.lamthoncoding.myfschoolse1913be.dto.UserDto;
import com.lamthoncoding.myfschoolse1913be.entity.Role;
import com.lamthoncoding.myfschoolse1913be.entity.User;
import com.lamthoncoding.myfschoolse1913be.exception.handlers.EntityNotFound;
import com.lamthoncoding.myfschoolse1913be.exception.handlers.InvalidInputException;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserCreateRequest;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserUpdateRequest;
import com.lamthoncoding.myfschoolse1913be.repository.RoleRepository;
import com.lamthoncoding.myfschoolse1913be.repository.UserRepository;
import com.lamthoncoding.myfschoolse1913be.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    // ==== Cấu hình cột file Excel (khớp thứ tự cột export) ====
    private static final int COL_STT = 0;
    private static final int COL_USERNAME = 1;
    private static final int COL_FULLNAME = 2;
    private static final int COL_EMAIL = 3;
    private static final int COL_PHONE = 4;
    private static final int COL_STATUS = 5;
    private static final int COL_ROLES = 6;
    private static final String DEFAULT_IMPORT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ==========================================================
    // ================     IMPORT / EXPORT     ================
    // ==========================================================

    @Override
    public List<UserDto> importUsers(MultipartFile file) {
        List<UserDto> responses = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) return responses;

            rows.next(); // bỏ qua header

            while (rows.hasNext()) {
                Row row = rows.next();
                importUserFromRow(row).ifPresent(responses::add);
            }
        } catch (Exception e) {
            log.error("Error importing users: ", e);
            throw new InvalidInputException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
        return responses;
    }

    /**
     * Đọc 1 dòng Excel, tạo mới user nếu số điện thoại chưa tồn tại.
     * Nếu số điện thoại đã tồn tại -> bỏ qua (không update, không insert).
     */
    private Optional<UserDto> importUserFromRow(Row row) {
        String username = getCellStringValue(row.getCell(COL_USERNAME));
        String phone = getCellStringValue(row.getCell(COL_PHONE));

        if (username.isEmpty() || phone.isEmpty()) {
            return Optional.empty();
        }

        if (userRepository.findByPhone(phone).isPresent()) {
            log.info("Bỏ qua vì số điện thoại đã tồn tại: {}", phone);
            return Optional.empty();
        }

        String fullName = getCellStringValue(row.getCell(COL_FULLNAME));
        String email = getCellStringValue(row.getCell(COL_EMAIL));
        String statusStr = getCellStringValue(row.getCell(COL_STATUS));
        String rolesStr = getCellStringValue(row.getCell(COL_ROLES));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(DEFAULT_IMPORT_PASSWORD))
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .status(parseUserStatus(statusStr))
                .build();
        user.setRoles(resolveRolesFromImport(rolesStr));

        User saved = userRepository.save(user);
        return Optional.of(mapToDto(saved));
    }

    private UserStatus parseUserStatus(String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) return UserStatus.ACTIVE;
        try {
            return UserStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (Exception e) {
            log.warn("Trạng thái không hợp lệ '{}', dùng mặc định ACTIVE", statusStr);
            return UserStatus.ACTIVE;
        }
    }

    private Set<Role> resolveRolesFromImport(String rolesStr) {
        Set<Role> roles = new HashSet<>();
        if (rolesStr != null && !rolesStr.isEmpty()) {
            for (String raw : rolesStr.split(",")) {
                String roleName = raw.trim();
                if (roleName.isEmpty()) continue;
                try {
                    roleRepository.findByName(RoleName.valueOf(roleName)).ifPresent(roles::add);
                } catch (Exception e) {
                    log.warn("Quyền không hợp lệ trong file import: {}", roleName);
                }
            }
        }
        if (roles.isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new EntityNotFound("Không tìm thấy quyền mặc định"));
            roles.add(defaultRole);
        }
        return roles;
    }

    @Override
    public byte[] exportUsers(String name, String phone) {
        List<User> users = userRepository.findUsersByFilters(name, phone, Pageable.unpaged()).getContent();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Danh sách người dùng");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Username", "Họ Tên", "Email", "Số điện thoại", "Trạng thái", "Quyền"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(COL_STT).setCellValue(rowIdx - 1);
                row.createCell(COL_USERNAME).setCellValue(user.getUsername());
                row.createCell(COL_FULLNAME).setCellValue(user.getFullName());
                row.createCell(COL_EMAIL).setCellValue(user.getEmail());
                row.createCell(COL_PHONE).setCellValue(user.getPhone());
                row.createCell(COL_STATUS).setCellValue(user.getStatus().name());
                row.createCell(COL_ROLES).setCellValue(
                        user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.joining(", "))
                );
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting users: ", e);
            throw new RuntimeException("Lỗi khi tạo file Excel");
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return "";
    }

    // ==========================================================
    // ================        CRUD USER        ================
    // ==========================================================

    @Override
    public UserDto getMyProfile(String phone) {
        log.info("GetMyProfile: {}", phone);
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy người dùng"));
        return mapToDto(user);
    }

    @Override
    public String changePassword(String newPass, String confirmPassword, String phone) {
        log.info("ChangePassword: {}", phone);
        if (!newPass.equals(confirmPassword)) {
            throw new InvalidInputException("Mật khẩu xác nhận không khớp");
        }
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy người dùng"));
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
        return "Đổi mật khẩu thành công";
    }

    @Override
    public Page<UserDto> getUsers(String name, String phone, Pageable pageable) {
        return userRepository.findUsersByFilters(name, phone, pageable).map(this::mapToDto);
    }

    @Override
    public UserDto createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidInputException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidInputException("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new InvalidInputException("Số điện thoại đã tồn tại");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .build();

        user.setRoles(resolveRolesFromRequest(request.getRoles()));

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy người dùng"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new InvalidInputException("Email đã tồn tại");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new InvalidInputException("Số điện thoại đã tồn tại");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        if (request.getRoles() != null) {
            user.setRoles(resolveRolesFromRequest(request.getRoles()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    /**
     * Bật/tắt trạng thái user thay vì xóa cứng khỏi DB.
     */
    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy người dùng"));

        UserStatus newStatus = user.getStatus() == UserStatus.ACTIVE
                ? UserStatus.INACTIVE
                : UserStatus.ACTIVE;
        user.setStatus(newStatus);

        User updated = userRepository.save(user);
        mapToDto(updated);
    }

    private Set<Role> resolveRolesFromRequest(Collection<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (RoleName roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFound("Không tìm thấy quyền: " + roleName));
                roles.add(role);
            }
        } else {
            Role userRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new EntityNotFound("Không tìm thấy quyền mặc định"));
            roles.add(userRole);
        }
        return roles;
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}