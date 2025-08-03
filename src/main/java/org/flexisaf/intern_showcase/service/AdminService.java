package org.flexisaf.intern_showcase.service;

import lombok.extern.slf4j.Slf4j;
import org.flexisaf.intern_showcase.dto.*;
import org.flexisaf.intern_showcase.mappers.AmbulanceMapper;
import org.flexisaf.intern_showcase.mappers.AmbulanceRequestMapper;
import org.flexisaf.intern_showcase.mappers.UserMapper;
import org.flexisaf.intern_showcase.mappers.UserRequestMapper;
import org.flexisaf.intern_showcase.model.AmbulanceModel;
import org.flexisaf.intern_showcase.model.AmbulanceRequestModel;
import org.flexisaf.intern_showcase.model.UserModel;
import org.flexisaf.intern_showcase.model.UserRequestModel;
import org.flexisaf.intern_showcase.repository.AmbulanceRepository;
import org.flexisaf.intern_showcase.repository.AmbulanceRequestRepository;
import org.flexisaf.intern_showcase.repository.UserRepository;
import org.flexisaf.intern_showcase.repository.UserRequestRepository;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Autowired
    private AmbulanceRequestRepository ambulanceRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmbulanceMapper ambulanceMapper;

    @Autowired
    private UserRequestMapper userRequestMapper;

    @Autowired
    private AmbulanceRequestMapper ambulanceRequestMapper;

    @Autowired
    private EmailInfo emailInfo;

    @Autowired
    private EmailService emailService;

    public void createAmbulance(AmbulanceDTO ambulanceDTO) {
        AmbulanceModel ambulanceModel = ambulanceMapper.convertToModel(ambulanceDTO);
        ambulanceRepository.save(ambulanceModel);

        log.info("created ambulance...");
    }

    public AmbulanceModel getAmbulance() {
        return ambulanceRepository.findByAvailabilityStatus("Available")
                .stream().findFirst().orElse(null);
    }

    public AmbulanceDTO getAmbulanceWithID(Long id) {
        return ambulanceRepository.findById(id)
                .map(ambulanceMapper::convertToDTO).orElse(new AmbulanceDTO());
    }

    public List<AmbulanceDTO> getAmbulanceList() {
        return ambulanceRepository.findAll()
                .stream().map(ambulanceMapper::convertToDTO).toList();
    }

    public List<AmbulanceDTO> getAvailableAmbulanceList() {
        return ambulanceRepository.findByAvailabilityStatus("Available")
                .stream().map(ambulanceMapper::convertToDTO).toList();
    }

    public void deleteAmbulanceWithID(Long id) {
        ambulanceRepository.deleteById(id);
        log.info("ambulance data deleted from repository");
    }

    public int getManagedUserCounts() {
        return userRepository.findAll().size();
    }

    public int getDispatchedAmbulance() {
        return (int) ambulanceRequestRepository.countByDispatchStatus(DispatchStatus.ASSIGNED.name());
    }

    public int getPendingApprovals() {
        return (int) userRequestRepository.countByRequestStatus(RequestStatus.PENDING.name());
    }

    public AmbulanceRequest requestForAmbulance(Long id, String email) {

        AmbulanceRequest ambulanceRequest = new AmbulanceRequest();
        AmbulanceModel ambulanceModel = getAmbulance();
        if(!Objects.isNull(ambulanceModel)) {

            UserRequestModel userRequestModel =
                    userRequestRepository.findById(id)
                            .orElse(null);
            if(!Objects.isNull(userRequestModel)) {
                if(!Objects.equals(userRequestModel.getRequestStatus(),RequestStatus.APPROVED.name())) {
                    userRequestModel.setRequestStatus(RequestStatus.APPROVED.name());

                    ambulanceRequest = AmbulanceRequest.builder()
                            .request_time(userRequestModel.getRequest_time())
                            .userDTO(userMapper.convertToDTO(userRequestModel.getUserModel()))
                            .request_status(userRequestModel.getRequestStatus())
                            .ambulanceDTO(ambulanceMapper.convertToDTO(ambulanceModel))
                            .dispatch_time(LocalTime.now())
                            //i cant calculate ETA since i don't have the ambulance coordinates
                            //ETA is a fixed time of 30 minutes
                            .arrivalTime(LocalTime.now().plusMinutes(30))
                            .dispatch_status(DispatchStatus.ASSIGNED.name())
                            .address(userRequestModel.getAddress())
                            .emergencyLevel(userRequestModel.getEmergencyLevel())
                            .build();

                    AmbulanceRequestModel ambulanceRequestModel =
                            new AmbulanceRequestModel();
                    ambulanceRequestModel = ambulanceRequestMapper.convertToModel(ambulanceRequest);
                    ambulanceRequestModel.setAmbulanceModel(ambulanceModel);
                    ambulanceRequestModel.setDispatchStatus(DispatchStatus.ASSIGNED.name());
                    ambulanceRequestModel.setUserModel(userRequestModel.getUserModel());

                    UserRequest userRequest =
                            UserRequest.builder()
                                    .userDTO(userMapper.convertToDTO(userRequestModel.getUserModel()))
                                    .emergencyLevel(userRequestModel.getEmergencyLevel())
                                    .address(userRequestModel.getAddress())
                                    .build();
                    emailService.sendEmail(userRequestModel.getUserModel().getEmail(),emailInfo.ambulanceRequestMessage(userRequest,ambulanceRequest),"Ambulance Request Approved - Help is on the way");
                    log.info("mail has been sent to user");

                    ambulanceRequestRepository.save(ambulanceRequestModel);
                    log.info("Ambulance request data saved to db");

                    userRequestRepository.save(userRequestModel);
                    log.info("user request saved to db..");
                    ambulanceModel.setAvailabilityStatus("Unavailable");
                    ambulanceRepository.save(ambulanceModel);
                    log.info("updated ambulance data.....");

                    hasAmbulanceArrived(ambulanceRequest,userRequestModel.getUserModel().getEmail()); // scheduler to notify user if ambulance has arrived                return ambulanceRequest;
                }
                ambulanceRequest.setError("No need for ambulance!, Request Has Been Approved Already");
                return ambulanceRequest;
            }
            return null;
        }
        ambulanceRequest.setError("There is no available ambulance, try creating more ambulance...");
        return ambulanceRequest;
    }

    public String remindAdminOnRequest(Long id, String email) {
        UserRequestModel userRequestModel =
                userRequestRepository.findById(id).orElse(new UserRequestModel());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        String time = userRequestModel.getRequest_time();
        LocalTime requestTime = LocalTime.parse(time,dateTimeFormatter);

        LocalTime currentTime = LocalTime.now();
        boolean result = currentTime.isAfter(requestTime.plusMinutes(15)); //user can only track request after 15 minutes of making that request

        if(!userRequestModel.getRequestStatus().equals(RequestStatus.APPROVED.name())) {
            if(result) {
                //send request to admin
                emailService.sendEmail(email,emailInfo.remindAdmin(userRequestMapper.convertToDTO(userRequestModel)),
                        "User Request Reminder For Ambulance");
                log.info("reminder messaged sent..");
                return "Reminder Message Has been Sent To Admin";
            }
            else {
                return "You Can Only Remind Admin After 15minutes of making a request..";
            }
        }
        else {
            return "Request Has Already Been Approved";
        }
    }

    public void hasAmbulanceArrived(AmbulanceRequest ambulanceRequest, String email) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        long delay = Duration.between(LocalTime.now(), ambulanceRequest.getArrivalTime()).toMillis();

        scheduler.schedule(() -> {
            // Do work here: notify, release ambulance
            emailService.sendEmail(
                    email,
                    emailInfo.ambulanceArrived(ambulanceRequest),
                    "Ambulance Has Reached – Emergency Request Completed"
            );
        }, delay, TimeUnit.MILLISECONDS);

    }

    public AmbulanceRequest getAmbulanceRequestByID(Long id) {
        AmbulanceRequestModel ambulanceRequestModel =
                ambulanceRequestRepository.findById(id).orElse(new AmbulanceRequestModel());
        AmbulanceRequest ambulanceRequest =ambulanceRequestMapper.convertToDTO(ambulanceRequestModel);
        ambulanceRequest.setAmbulanceDTO(ambulanceMapper.convertToDTO(ambulanceRequestModel.getAmbulanceModel()));
        ambulanceRequest.setUserDTO(userMapper.convertToDTO(ambulanceRequestModel.getUserModel()));

        return ambulanceRequest;
    }
}
