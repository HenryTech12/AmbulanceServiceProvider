package org.flexisaf.intern_showcase.controller;

import jakarta.validation.Valid;
import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.LoginRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.flexisaf.intern_showcase.response.UserResponse;
import org.flexisaf.intern_showcase.service.AdminService;
import org.flexisaf.intern_showcase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-service")
public class ApiController {


    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/account/create")
    public ResponseEntity<UserResponse> createAccount(@RequestBody @Valid UserDTO userDTO) {
            UserDTO data = userService.createUser(userDTO);
            return new ResponseEntity<>(UserResponse.builder()
                    .data(userDTO)
                    .message("User Created")
                    .status("success")
                    .build(), HttpStatus.OK);
    }
    @PostMapping("/login")
    public String logIn(@RequestBody LoginRequest loginRequest) {
        return "loggedIn";
    }

    @PostMapping("/account/request/create")
    public ResponseEntity<UserRequest> createRequest(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.createUserRequest(userRequest,userRequest.getUserDTO().getEmail()), HttpStatus.OK);
    }

    @GetMapping("/account/requests/all")
    public ResponseEntity<List<UserRequest>> getAllRequests() {
        return new ResponseEntity<>(userService.getUserRequests(), HttpStatus.OK);
    }

    @GetMapping("/account/{status}/requests")
    public ResponseEntity<List<UserRequest>> getRequestsByStatus(@PathVariable String status) {
        return new ResponseEntity<>(userService.getUserRequest(status),HttpStatus.OK);
    }

    @GetMapping("/account/get/email/{email}")
    public ResponseEntity<UserDTO> getAccountByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserWithEmail(email),HttpStatus.OK);
    }

    @GetMapping("/account/get/id/{id}")
    public ResponseEntity<UserDTO> getAccountWithID(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserWithID(id),HttpStatus.OK);
    }

    @DeleteMapping("/account/delete/id/{id}")
    public ResponseEntity<UserResponse> deleteAccountWithID(@PathVariable Long id) {
        return new ResponseEntity<>(userService.deleteUserWithID(id),HttpStatus.OK);
    }

    @PutMapping("/account/{id}/update")
    public ResponseEntity<UserDTO> updateAccount(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(userService.updateAccount(id,userDTO),HttpStatus.OK);
    }



    @PostMapping("/ambulance/create")
    public ResponseEntity<String> createAmbulance(@RequestBody AmbulanceDTO ambulanceDTO) {
        adminService.createAmbulance(ambulanceDTO);
        return new ResponseEntity<>("Ambulance Created Successfully",HttpStatus.OK);
    }

    @PostMapping("/ambulance/request/assign/{requestId}")
    public ResponseEntity<AmbulanceRequest> assignAmbulance(@PathVariable Long requestId , @RequestBody @Valid AmbulanceRequest ambulanceRequest) {
        return new ResponseEntity<>(adminService.requestForAmbulance(requestId,ambulanceRequest.getUserDTO().getEmail()),HttpStatus.OK);
    }

    @GetMapping("/ambulance/all")
    public ResponseEntity<List<AmbulanceDTO>> getAllAmbulance() {
        return new ResponseEntity<>(adminService.getAmbulanceList(), HttpStatus.OK);
    }

    @GetMapping("/ambulance/get/id/{id}")
    public ResponseEntity<AmbulanceDTO> getAmbulanceWithID(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.getAmbulanceWithID(id),HttpStatus.OK);
    }

    @DeleteMapping("/ambulance/delete/id/{id}")
    public ResponseEntity<String> deleteAmbulanceWithID(@PathVariable Long id) {
        adminService.deleteAmbulanceWithID(id);
        return new ResponseEntity<>("Ambulance Data Deleted Successfully",HttpStatus.OK);
    }

}
