package playground.controller;

import org.springframework.web.bind.annotation.*;
import playground.domain.user.User;
import playground.exceptions.NotFoundException;
import playground.json.view.StatusChangeRequest;
import playground.json.view.StatusChangeResponse;
import playground.json.view.UserAdditionResponse;
import playground.service.user.UserService;

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
