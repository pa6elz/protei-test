package com.github.alex353cay.protei.test.controller;

import com.github.alex353cay.protei.test.domain.user.User;
import com.github.alex353cay.protei.test.exceptions.NotFoundException;
import com.github.alex353cay.protei.test.json.view.StatusChangeRequest;
import com.github.alex353cay.protei.test.json.view.StatusChangeResponse;
import com.github.alex353cay.protei.test.json.view.UserAdditionResponse;
import com.github.alex353cay.protei.test.service.user.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable(name = "id") long id) {
        return userService.getUser(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping("/users")
    public UserAdditionResponse addUser(@RequestBody User user) {
        return new UserAdditionResponse(userService.addUser(user).getId().orElseThrow(IllegalStateException::new));
    }

    @PutMapping("/users/{id}")
    public StatusChangeResponse updateStatus(@PathVariable(name = "id") long id, @RequestBody StatusChangeRequest body) {
        User user = getUser(id);
        User.Status previousStatus = user.getStatus();
        user.setStatus(body.getStatus());
        user = userService.update(user);
        return new StatusChangeResponse(user.getId().get(), previousStatus, user.getStatus());
    }
}
