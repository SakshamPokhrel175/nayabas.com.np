package com.nayabas.controller;

import com.nayabas.entity.Property;
import com.nayabas.entity.User;
import com.nayabas.service.PropertyService;
import com.nayabas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PropertyService propertyService;

    // ================= ALL SELLERS =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sellers")
    public List<User> allSellers() {
        return userService.findAllSellers();
    }

    // ================= ALL BUYERS =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buyers")
    public List<User> getAllBuyers() {
        return userService.findAllBuyers();
    }

    // ================= PENDING SELLERS =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sellers/pending")
    public List<User> pendingSellers() {
        return userService.findPendingSellers();
    }

    // ================= DASHBOARD STATS =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public Map<String, Long> stats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalSellers", userService.countSellers());
        stats.put("totalBuyers", userService.countBuyers());
        stats.put("pendingVerifications", userService.countPendingSellers());
        return stats;
    }

    // ================= ALL PROPERTIES WITH OWNER =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/properties")
    public List<Map<String, Object>> allProperties() {
        return propertyService.getAllProperties().stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("title", p.getTitle());
                    map.put("city", p.getCity());
                    map.put("price", p.getPrice());
                    map.put("bedrooms", p.getBedrooms());

                    Map<String, Object> owner = new HashMap<>();
                    owner.put("id", p.getOwner().getId());
                    owner.put("username", p.getOwner().getUsername());
                    owner.put("email", p.getOwner().getEmail());
                    owner.put("fullName", p.getOwner().getFullName());
                    map.put("owner", owner);

                    return map;
                })
                .collect(Collectors.toList());
    }

    // ================= APPROVE SELLER =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/sellers/{id}/approve")
    public User approveSeller(@PathVariable Long id) {
        return userService.verifySeller(id, true, "Approved by admin");
    }

    // ================= REJECT SELLER =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/sellers/{id}/reject")
    public User rejectSeller(@PathVariable Long id) {
        return userService.verifySeller(id, false, "Rejected by admin");
    }
}
