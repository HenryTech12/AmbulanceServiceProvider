package org.flexisaf.intern_showcase.service;

import lombok.extern.slf4j.Slf4j;
import org.flexisaf.intern_showcase.dto.EmailInfo;
import org.flexisaf.intern_showcase.dto.RequestStatus;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.mappers.UserMapper;
import org.flexisaf.intern_showcase.mappers.UserRequestMapper;
import org.flexisaf.intern_showcase.model.UserModel;
import org.flexisaf.intern_showcase.model.UserRequestModel;
import org.flexisaf.intern_showcase.repository.UserRepository;
import org.flexisaf.intern_showcase.repository.UserRequestRepository;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.ResetPasswordRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.flexisaf.intern_showcase.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRequestMapper userRequestMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailInfo emailInfo;
    public UserDTO createUser(UserDTO userDTO) {
        if(!Objects.isNull(userDTO)) {
            UserModel userModel = new UserModel();
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userModel = userMapper.convertToModel(userDTO);

            userRepository.save(userModel);

            log.info("user details saved to db.");
        }
        return userDTO;
    }


    public UserRequest createUserRequest(UserRequest userRequest, String email) {

        UserModel userModel = userRepository.findByEmail(email).orElse(new UserModel());
        if(!Objects.isNull(userRequest)) {
            UserRequestModel userRequestModel = new UserRequestModel();
            userRequestModel = userRequestMapper.convertToModel(userRequest);
            userRequestModel.setUserModel(userModel);
            userRequestModel.setRequestStatus(RequestStatus.PENDING.name());
            userRequestModel.setRequest_time(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));

            userRequest.setRequestStatus(userRequestModel.getRequestStatus());
            userRequest.setRequest_time(userRequestModel.getRequest_time());
            emailService.sendEmail(email,emailInfo.userRequestMessage(userRequest),"Ambulance Request Received - We're on itpm");

            userRequestRepository.save(userRequestModel);
            log.info("user ambulance request info saved to db.");

            return userRequest;
        }
        return null;
    }


    public UserDTO getUserWithEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::convertToDTO).orElse(new UserDTO());
    }

    public UserDTO getUserWithID(Long id) {
        return userRepository.findById(id)
                .map(userMapper::convertToDTO).orElse(new UserDTO());
    }

    public UserResponse deleteUserWithID(Long id) {
        userRepository.deleteById(id);
        return UserResponse.builder()
                .status("deleted")
                .message("Account Deleted")
                .data(getUserWithID(id))
                .build();
    }

    public void deleteUserRequestWithID(Long id) {
        userRequestRepository.deleteById(id);
        log.info("user request removed from db");
    }

    public UserDTO updateAccount(Long id, UserDTO userDTO) {
        UserModel userModel =
                userRepository.findById(id).orElse(null);
        if(!Objects.isNull(userDTO)) {
            userModel = userMapper.convertToModel(userDTO);
            userRepository.save(userModel); // user account updated...
        }
        return userDTO;
    }

    public List<UserRequest> getUserRequest(String status) {
        return userRequestRepository
                .findByRequestStatus(status)
                .stream().map(data -> UserRequest.builder()
                        .id(data.getId())
                        .request_time(data.getRequest_time())
                        .requestStatus(data.getRequestStatus())
                        .emergencyLevel(data.getEmergencyLevel())
                        .emergencyDescription(data.getEmergencyDescription())
                        .latitude(data.getLatitude())
                        .longtitude(data.getLongtitude())
                        .address(data.getAddress())
                        .userDTO(userMapper.convertToDTO(data.getUserModel()))
                        .build())
                .toList();
    }

    public List<UserRequest> getUserRequests() {
        return userRequestRepository.findAll()
                .stream().map(data -> UserRequest.builder()
                        .id(data.getId())
                        .request_time(data.getRequest_time())
                        .requestStatus(data.getRequestStatus())
                        .emergencyLevel(data.getEmergencyLevel())
                        .emergencyDescription(data.getEmergencyDescription())
                        .latitude(data.getLatitude())
                        .longtitude(data.getLongtitude())
                        .address(data.getAddress())
                        .userDTO(userMapper.convertToDTO(data.getUserModel()))
                        .build())
                .toList();
    }

    public boolean cancelRequest(Long id) {
        UserRequestModel userRequestModel =
                userRequestRepository.findById(id).orElse(new UserRequestModel());
        return userRequestModel.getRequestStatus().equals(RequestStatus.PENDING.name());
    }

    public boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UserModel userModel =
                userRepository.findByEmail(resetPasswordRequest.getEmail()).orElse(null);

        if(!Objects.isNull(userModel)) {
            userModel.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

            userRepository.save(userModel);

            log.info("new password saved to db...");
            return true;
        }
        return false;
    }
}
