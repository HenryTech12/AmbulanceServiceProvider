package org.flexisaf.intern_showcase.controller;

import jakarta.validation.Valid;
import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.dto.RequestStatus;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.flexisaf.intern_showcase.service.AdminService;
import org.flexisaf.intern_showcase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/api")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model, String error) {
        model.addAttribute("userRequest", new UserRequest());

        List<UserRequest> allRequest = userService.getUserRequests();
        if(allRequest.isEmpty()) {
            model.addAttribute("userRequests", new ArrayList<>());
        }
        else {
            model.addAttribute("userRequests", userService.getUserRequests());
        }

        List<UserRequest> pendingRequest = userService.getUserRequest(RequestStatus.PENDING.name());


        if(!pendingRequest.isEmpty()) {
            model.addAttribute("pendingRequestList", pendingRequest);
        }
        else{
            model.addAttribute("pendingRequestList", new ArrayList<>());
        }

        List<AmbulanceDTO> ambulanceDTOList = adminService.getAvailableAmbulanceList();
        if(!ambulanceDTOList.isEmpty()) {
            System.out.println("size: "+ambulanceDTOList.size());
            model.addAttribute("ambulanceList",ambulanceDTOList);
        }
        else {
            model.addAttribute("ambulanceList",new ArrayList<>());
        }

        List<UserRequest> activeRequest = userService.getUserRequest(RequestStatus.APPROVED.name());
        if(!activeRequest.isEmpty()) {
            model.addAttribute("activeRequestList", activeRequest);
        }
        else{
            model.addAttribute("activeRequestList", new ArrayList<>());
        }
        model.addAttribute("error",error);
        return "admin_dashboard";
    }

    @GetMapping("/user/request/pending")
    public RedirectView getPendingRequest(Model model) {
        return new RedirectView("/admin/api/dashboard");
    }

    @GetMapping("/profile")
    public String checkProfile(Model model, Principal principal) {
        model.addAttribute("user_managed",adminService.getManagedUserCounts());
        model.addAttribute("ambulance_dispatched",adminService.getDispatchedAmbulance());
        model.addAttribute("pending_approvals",adminService.getPendingApprovals());
        model.addAttribute("userDTO",userService.getUserWithEmail(principal.getName()));

        return "admin_profile";
    }

    @GetMapping("/profile/edit")
    @ResponseBody
    public String editAdminProfile() {
        return "Not yet implemented";
    }

    @GetMapping("/user/request/approved")
    public String getApprovedRequest(Model model) {
        model.addAttribute("userRequestList",userService.
                getUserRequest(RequestStatus.APPROVED.name()));
        model.addAttribute("userRequest", new UserRequest());
        return "pending_notification";
    }


    @PostMapping("/ambulance/create")
    public RedirectView createAmbulance(@Valid AmbulanceDTO ambulanceDTO) {
        adminService.createAmbulance(ambulanceDTO);
        return new RedirectView("/admin/api/dashboard");
    }

    @PostMapping("/ambulance/assign/{id}")
    public RedirectView assignAmbulanceToUser(@PathVariable Long id, Model model,
                                              Principal principal, RedirectAttributes redirectAttributes) {
        AmbulanceRequest ambulanceRequest = adminService.requestForAmbulance(id,principal.getName());
        if(ambulanceRequest.getUserDTO() != null) {
            if(!ambulanceRequest.getRequest_status().equals(RequestStatus.APPROVED.name())) {
                redirectAttributes.addFlashAttribute("request", ambulanceRequest);
                return new RedirectView("/admin/api/ambulance/assign/info");
            }
            else {
                String error = "No need for ambulance!, Request Has Been Approved Already";
                return new RedirectView("/admin/api/dashboard?error="+ambulanceRequest.getError());
            }
        }
        else {
            model.addAttribute("request", new AmbulanceRequest());
            String error = "There is no available ambulance, try creating more ambulance...";
            return new RedirectView("/admin/api/dashboard?error="+ambulanceRequest.getError());

        }
    }

    @GetMapping("/ambulance/assign/info")
    public String assignedAmbulanceInfo(Model model) {

        AmbulanceRequest ambulanceRequest = (AmbulanceRequest)
                model.getAttribute("request");
        if(ambulanceRequest != null) {
            model.addAttribute("request",ambulanceRequest);
        }
        else{
            throw new RuntimeException("Request Not Found....");
        }
        return "assign_ambulance_info";
    }

    @PostMapping("/ambulance/track/{requestId}")
    public String trackAmbulance(@PathVariable Long requestId,Model model) {
        AmbulanceRequest ambulanceRequest = adminService.getAmbulanceRequestByID(requestId);
        if(ambulanceRequest != null) {
            model.addAttribute("request",ambulanceRequest);
            model.addAttribute("msg", "Request With ID: "+requestId+" Data Found...");
        }
        else {
            model.addAttribute("msg", "Invalid ID, Request Not Found....");
        }
        return "assign_ambulance_info";
    }

    @GetMapping("/ambulance/all")
    public RedirectView getAllAmbulance(Model model) {
        model.addAttribute("ambulance", new AmbulanceDTO());

        return new RedirectView("/admin/api/dashboard");
    }

    @GetMapping("/ambulance/new")
    public String getAmbulancePage(Model model) {
        List<AmbulanceDTO> ambulanceDTOList = adminService.getAmbulanceList();
        if(!ambulanceDTOList.isEmpty()) {
            System.out.println("size: "+ambulanceDTOList.size());
            model.addAttribute("ambulanceList",ambulanceDTOList);
        }
        else {
            model.addAttribute("ambulanceList",new ArrayList<>());
        }
        model.addAttribute("ambulance", new AmbulanceDTO());
        return "ambulance";
    }
    @GetMapping("/ambulance/new/create")
    public String ambulanceCreatePage(Model model) {
        model.addAttribute("ambulance", new AmbulanceDTO());
        return "create_ambulance";
    }
}
