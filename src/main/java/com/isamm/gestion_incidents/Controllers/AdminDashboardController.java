package com.isamm.gestion_incidents.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}