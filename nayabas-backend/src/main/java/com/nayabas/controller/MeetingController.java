package com.nayabas.controller;

import com.nayabas.dto.MeetingRequest;
import com.nayabas.dto.MeetingResponse;
import com.nayabas.dto.MeetingUpdateSellerRequest; // 💡 NEW IMPORT
import com.nayabas.entity.Meeting;
import com.nayabas.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    /** 🧩 Customer requests a meeting */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Meeting> requestMeeting(@RequestBody MeetingRequest request, Principal principal) {
        // Return ResponseEntity.ok() for standard response handling
        return ResponseEntity.ok(meetingService.createMeeting(request, principal.getName()));
    }

    /** 🧩 Seller updates meeting status (SCHEDULED or REJECTED) */
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{meetingId}/status")
    public ResponseEntity<Meeting> updateStatus(@PathVariable Long meetingId, @RequestParam String status) {
        return ResponseEntity.ok(meetingService.updateStatus(meetingId, status));
    }

    // 💡 NEW ENDPOINT: Seller proposes a new date/time
    @PutMapping("/{meetingId}/propose-change")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<MeetingResponse> proposeMeetingChange(@PathVariable Long meetingId,
                                                               @RequestBody MeetingUpdateSellerRequest request,
                                                               Principal principal) {
        MeetingResponse updated = meetingService.proposeChange(
            meetingId,
            request.getNewDate(),
            request.getNewTime(),
            request.getSellerNote(),
            principal.getName()
        );
        return ResponseEntity.ok(updated);
    }

    // 💡 NEW ENDPOINT: Customer confirms the seller's proposed date/time
    @PutMapping("/{meetingId}/confirm-change")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MeetingResponse> confirmProposedMeeting(@PathVariable Long meetingId,
                                                                 Principal principal) {
        MeetingResponse updated = meetingService.confirmProposedChange(meetingId, principal.getName());
        return ResponseEntity.ok(updated);
    }

//    /** 🧩 Customer views their meetings */
//    @PreAuthorize("hasRole('CUSTOMER')")
//    @GetMapping("/customer")
//    public List<MeetingResponse> customerMeetings(Principal principal) {
//        return meetingService.getMeetingsByCustomerResponse(principal.getName());
//    }
//
//    /** 🧩 Seller views meetings for their properties */
//    @PreAuthorize("hasRole('SELLER')")
//    @GetMapping("/seller")
//    public List<MeetingResponse> sellerMeetings(Principal principal) {
//        return meetingService.getMeetingsBySeller(principal.getName());
//    }
    
    /** 🧩 Customer views their meetings (FIXED RETURN TYPE) */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer")
    public ResponseEntity<List<MeetingResponse>> customerMeetings(Principal principal) {
        return ResponseEntity.ok(meetingService.getMeetingsByCustomerResponse(principal.getName()));
    }

    /** 🧩 Seller views meetings for their properties (FIXED RETURN TYPE) */
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<List<MeetingResponse>> sellerMeetings(Principal principal) {
        return ResponseEntity.ok(meetingService.getMeetingsBySeller(principal.getName()));
    }
}


//package com.nayabas.controller;
//
//import com.nayabas.dto.MeetingRequest;
//import com.nayabas.dto.MeetingResponse;
//import com.nayabas.entity.Meeting;
//import com.nayabas.service.MeetingService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/meetings")
//@RequiredArgsConstructor
//public class MeetingController {
//
//    private final MeetingService meetingService;
//
//    /** 🧩 Customer requests a meeting */
//    @PreAuthorize("hasRole('CUSTOMER')")
//    @PostMapping
//    public Meeting requestMeeting(@RequestBody MeetingRequest request, Principal principal) {
//        return meetingService.createMeeting(request, principal.getName());
//    }
//
//    /** 🧩 Seller updates meeting status */
//    @PreAuthorize("hasRole('SELLER')")
//    @PutMapping("/{meetingId}/status")
//    public Meeting updateStatus(@PathVariable Long meetingId, @RequestParam String status) {
//        return meetingService.updateStatus(meetingId, status);
//    }
//
//    /** 🧩 Customer views their meetings */
//    @PreAuthorize("hasRole('CUSTOMER')")
//    @GetMapping("/customer")
//    public List<MeetingResponse> customerMeetings(Principal principal) {
//        return meetingService.getMeetingsByCustomerResponse(principal.getName());
//    }
//
//    /** 🧩 Seller views meetings for their properties */
//    @PreAuthorize("hasRole('SELLER')")
//    @GetMapping("/seller")
//    public List<MeetingResponse> sellerMeetings(Principal principal) {
//        return meetingService.getMeetingsBySeller(principal.getName());
//    }
//}
