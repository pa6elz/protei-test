package com.github.alex353cay.protei.test.service.user;

import com.github.alex353cay.protei.test.domain.user.User;
import com.github.alex353cay.protei.test.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern phoneNumberValidationPattern = Pattern.compile("^((\\+7)|8)\\d{10}$");
    private static final Pattern emailValidationPattern = Pattern.compile("^.+@.+\\..+$");
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getAllUsers() {
        final Iterable<User> users = userRepository.findAll();
        users.forEach(this::correctUserStatusAndLastTimeOnlineStatusHasBeenSet);
        return users;
    }

    public Optional<User> getUser(long id) {
        final Optional<User> user = userRepository.findById(id);
        user.ifPresent(this::correctUserStatusAndLastTimeOnlineStatusHasBeenSet);
        return user;
    }

    public User addUser(User user) {
        if (!isEmailCorrect(user) || !isUsersPhoneNumberCorrect(user)) throw new IllegalArgumentException();
        user.setId(null);
        user.setStatus(User.Status.Online);
        user.setLastTimeOnlineStatusHasBeenSet(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User update(User user) {
        if (!user.getId().isPresent() || !isEmailCorrect(user) || !isUsersPhoneNumberCorrect(user)) throw new IllegalArgumentException();
        if (user.getStatus() == User.Status.Online) user.setLastTimeOnlineStatusHasBeenSet(LocalDateTime.now());
        else user.setLastTimeOnlineStatusHasBeenSet(null);
        return userRepository.save(user);
    }

    private boolean isEmailCorrect(User user) {
        return (!user.getEmail().isPresent()) || emailValidationPattern.matcher(user.getEmail().get()).matches();
    }

    private boolean isUsersPhoneNumberCorrect(User user) {
        return (!user.getPhoneNumber().isPresent()) || phoneNumberValidationPattern.matcher(user.getPhoneNumber().get()).matches();
    }

    private void correctUserStatusAndLastTimeOnlineStatusHasBeenSet(User user) {
        if (user.getStatus() == User.Status.Online) {
            final boolean changeStatus = user
                    .getLastTimeOnlineStatusHasBeenSet()
                    .orElseThrow(IllegalStateException::new)
                    .plusMinutes(5)
                    .isBefore(LocalDateTime.now());
            if (changeStatus) {
                user.setStatus(User.Status.Away);
                user.setLastTimeOnlineStatusHasBeenSet(null);
            }
        } else user.setLastTimeOnlineStatusHasBeenSet(null);
        update(user);
    }
}
