package org.flexisaf.intern_showcase.dto;

import org.flexisaf.intern_showcase.model.AmbulanceRequestModel;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.springframework.stereotype.Component;

@Component
public class EmailInfo {

    public String userRequestMessage(UserRequest userRequest) {
        return String.format("""
                Dear %s,
                
                Thank you for submitting your ambulance request. Our emergency dispatch team has
                received the following details:
                
                Patient Information:
                    Name: %s,
                    Contact Number: %s,
                    Emergency Description: %s,
                    Location: %s,
                    Request Time: %s
                    
                    Status: %s Dispatch
                    
                    Our Team is reviewing your request and will assign the nearest available ambulance shortly.
                    You will receive another email once the ambulance is dispatched with the estimated arrival time.
                    
                    Thank you for your patience, Help is on the way.""", userRequest.getUserDTO().getFullname()
        ,userRequest.getUserDTO().getFullname(),userRequest.getUserDTO().getContactNum(),userRequest.getEmergencyDescription()
        ,userRequest.getAddress(), userRequest.getRequest_time(), userRequest.getRequestStatus());
    }

    public String ambulanceRequestMessage(UserRequest userRequest, AmbulanceRequest ambulanceRequest) {
        return String.format("""
                Dear %s
                
                Your ambulance request has been successfully approved and dispatched.
                
                Ambulance Details:
                  Ambulance ID: %s,
                  Estimated Time Of Arrival: %s,
                  Dispatch Time: %s
                  
                Patient Info:
                   Name: %s,
                   Contact: %s,
                   Emergency Level: %s,
                   Location: %s
                   
                Our team is on its way and will arrived shortly. Please stay calm and be ready
                to assist the paramedics upon arrival.
                
                Stay safe.
                **Emergency Dispatch Team**,
                Ambulance Service Provider
                """, userRequest.getUserDTO().getFullname(), ambulanceRequest.getAmbulanceDTO().getAmbulance_id(), ambulanceRequest.getArrivalTime(),
                ambulanceRequest.getDispatch_time(), userRequest.getUserDTO().getFullname(), userRequest.getUserDTO().getContactNum(),
                userRequest.getEmergencyLevel(),userRequest.getAddress());
    }
    public String remindAdmin(UserRequest userRequest) {
        return String.format("""
                
                Dear Admin,
                
                I hope this message finds you well.
                
                I am writing to kindly remind you about my ambulance request submitted on [Insert Date], which is still marked as pending. The request was submitted for [brief reason or emergency level if applicable], and I would appreciate an update on its status.
                
                Request Details:
                - Request ID: #%d
                - Location: %s
                - Time of Request: %s
                - Emergency Level: %s
                
                I understand you may be handling multiple requests, but I would be grateful if you could look into this matter and let me know if any further information is needed from my side.
                
                Thank you for your assistance and support.
                
                Best regards, \s
                %s \s
                %s \s
                %s
                
                """,userRequest.getId(),userRequest.getAddress(),userRequest.getRequest_time()
        ,userRequest.getEmergencyLevel(),userRequest.getUserDTO().getFullname(),
                userRequest.getUserDTO().getContactNum(),userRequest.getUserDTO().getEmail());
    }


    public String ambulanceArrived(AmbulanceRequest ambulanceRequest) {
        return String.format("""
                    Dear %s,
                
                        We are writing to inform you that the ambulance assigned to your emergency request has reached the estimated time of arrival (ETA).
                
                        Request Details:
                        Ambulance ID: %s
                        Emergency Level: %s
                        Location: %s
                        Dispatch Time: %s
                        Arrival Time: %s
                
                        If you need any further assistance, feel free to reach out to our support or submit another request if necessary.
                
                        Thank you for using our emergency response service.
                
                        Best regards,
                        Ambulance Dispatch Team
                """, ambulanceRequest.getUserDTO().getFullname(),
                ambulanceRequest.getAmbulanceDTO().getAmbulance_id(),
                ambulanceRequest.getEmergencyLevel(),ambulanceRequest.getAddress(),
                ambulanceRequest.getDispatch_time(),ambulanceRequest.getArrivalTime());
    }
}
