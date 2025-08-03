package org.flexisaf.intern_showcase.controller;

import jakarta.validation.Valid;
import org.flexisaf.intern_showcase.dto.RequestStatus;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.LoginRequest;
import org.flexisaf.intern_showcase.request.ResetPasswordRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.flexisaf.intern_showcase.service.AdminService;
import org.flexisaf.intern_showcase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("error", "Bad Credentials");
        return "login";
    }
    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "signup";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute @Valid LoginRequest loginRequest) {
        return "login";
    }

    @GetMapping("/logout-ui")
    public String logoutPage() {
        return "logout";
    }

    @PostMapping("/reset-password")
    public RedirectView resetPassword(@ModelAttribute @Valid ResetPasswordRequest data, RedirectAttributes redirectAttributes) {
        if(userService.resetPassword(data)) {
            redirectAttributes.addAttribute("msg", "Password Reset Successfully");
        }
        else {
            redirectAttributes.addAttribute("msg", "Invalid Reset Password Details");
        }
        return new RedirectView("/api/user/signin");
    }

    @GetMapping("/reset-ui")
    public String resetPasswordPage(Model model) {
        model.addAttribute("data", new ResetPasswordRequest());
        return "reset_password";
    }

    @GetMapping("/signin")
    public String signInPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "logged Out";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute @Valid UserDTO userDTO, Model model) {
        userService.createUser(userDTO);
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/request")
    public String getRequestPage(Model model, Principal principal) {
        UserDTO userDTO = userService.getUserWithEmail(principal.getName());
        model.addAttribute("fullname",userDTO.getFullname());
        model.addAttribute("userRequest", new UserRequest());
        return "ambulance_request";
    }

    @PostMapping("/request/{id}/reminder")
    public RedirectView remindUser(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        String message = adminService.remindAdminOnRequest(id,principal.getName());
        redirectAttributes.addAttribute("msg",message);
        return new RedirectView("/api/user/requests/track");
    }
    @GetMapping("/requests/track")
    public String trackRequest(Model model) {
        model.addAttribute("requests", userService.getUserRequests());
        model.addAttribute("request", new UserRequest());

        return "track_user_request";
    }

    @PostMapping("/request/cancel/{id}")
    public RedirectView cancelRequest(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean result = userService.cancelRequest(id);
        if(result) {
            userService.deleteUserRequestWithID(id); //delete request from db
            redirectAttributes.addAttribute("msg", "Your Request Has Been Cancelled....");
        }
        else {
            redirectAttributes.addAttribute("msg", "Request Has Been Approved");
        }
        return new RedirectView("/api/user/requests/track");
    }

    @PostMapping("/request/delete/{id}")
    public RedirectView deleteTrackRequest(@PathVariable Long id) {
        userService.deleteUserRequestWithID(id);
        return new RedirectView("/api/user/requests/track");
    }

    @GetMapping("/ambulance/request/pending")
    public String requestSuccess(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "pending_notification";
    }


    @PostMapping("/ambulance/request")
    public RedirectView requestAmbulance(@ModelAttribute @Valid UserRequest userRequest, Principal principal) {
        System.out.println("functionalities needs to be implemented");
        System.out.println(principal.getName());
        userService.createUserRequest(userRequest,principal.getName());
        return new RedirectView("/api/user/ambulance/request/pending");
    }
}
