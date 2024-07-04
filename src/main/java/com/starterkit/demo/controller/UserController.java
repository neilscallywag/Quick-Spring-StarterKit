package com.starterkit.demo.controller;

import com.starterkit.demo.features.FeatureToggle;
import com.starterkit.demo.model.User;
import com.starterkit.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.togglz.core.manager.FeatureManager;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FeatureManager featureManager;


    @GetMapping
    public List<User> getUsers() {
        if(featureManager.isActive(FeatureToggle.ANOTHER_FEATURE)) {
            return userService.getAllUsers();
        }
        else {
            return new ArrayList<>();
        }
    }
}
