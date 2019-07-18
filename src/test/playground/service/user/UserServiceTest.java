package playground.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import playground.domain.user.User;
import playground.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        final UserRepository userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);

        final User user1 = new User(1);
        final User user2 = User
                .builder()
                .id(2L)
                .status(User.Status.Online)
                .lastTimeOnlineStatusHasBeenSet(LocalDateTime.now().minusMinutes(6))
                .build();
        final User user3 = User
                .builder()
                .id(3L)
                .status(User.Status.Offline)
                .lastTimeOnlineStatusHasBeenSet(LocalDateTime.now())
                .build();
        final User user4 = User
                .builder()
                .id(4L)
                .status(User.Status.Away)
                .build();

        when(userRepository.findAll()).thenAnswer(invocation -> {
            final List<User> usersReceivedFromDB = new ArrayList<>();
            usersReceivedFromDB.add(user1);
            usersReceivedFromDB.add(user2);
            usersReceivedFromDB.add(user3);
            usersReceivedFromDB.add(user4);
            return usersReceivedFromDB;
        });

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        when(userRepository.findById(4L)).thenReturn(Optional.of(user4));
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> {
            final User user = (User) invocation.getArguments()[0];
            user.setId(0L);
            return user;
        });
    }

    @Test
    void getAllUsersReturnsAllUsersInADatabase() {
        final long[] users = StreamSupport.stream(userService.getAllUsers().spliterator(), false)
                .mapToLong(user -> {
                    assertTrue(user.getId().isPresent());
                    return user.getId().get();
                }).toArray();

        assertArrayEquals(users, new long[]{1, 2, 3, 4});
    }

    @Test
    void getAllUsersReturnsUsersWithCorrectStatusAndLastTimeOnlineStatusHasBeenSet() {
        final Iterable<User> users = userService.getAllUsers();

        users.forEach(user -> {
            assertTrue(user.getId().isPresent());
            if (user.getId().get() == 1) {
                assertEquals(user.getStatus(), User.Status.Online);
                assertTrue(user.getLastTimeOnlineStatusHasBeenSet().isPresent());
            }
            if (user.getId().get() == 2) assertEquals(user.getStatus(), User.Status.Away);
            if (user.getId().get() == 3) assertEquals(user.getStatus(), User.Status.Offline);
            if (user.getId().get() == 4) assertEquals(user.getStatus(), User.Status.Away);
            if (user.getId().get() != 1) assertFalse(user.getLastTimeOnlineStatusHasBeenSet().isPresent());
        });
    }

    @Test
    void getPresentedInADatabaseUserReturnsRequestedUser() {
        final Optional<User> user = userService.getUser(2);
        assertTrue(user.isPresent());
        assertTrue(user.get().getId().isPresent());
        assertEquals(2, (long) user.get().getId().get());
    }

    @Test
    void getMissingInADatabaseUserReturnsOptionalEmpty() {
        final Optional<User> user = userService.getUser(5);
        assertFalse(user.isPresent());
    }

    @Test
    void getUserReturnsUserWithCorrectStatusAndLastTimeOnlineStatusHasBeenSet() {
        final User user1 = userService.getUser(1).get();
        final User user2 = userService.getUser(2).get();
        final User user3 = userService.getUser(3).get();
        final User user4 = userService.getUser(4).get();
        assertEquals(user1.getStatus(), User.Status.Online);
        assertEquals(user2.getStatus(), User.Status.Away);
        assertEquals(user3.getStatus(), User.Status.Offline);
        assertEquals(user4.getStatus(), User.Status.Away);

        assertTrue(user1.getLastTimeOnlineStatusHasBeenSet().isPresent());
        assertFalse(user2.getLastTimeOnlineStatusHasBeenSet().isPresent());
        assertFalse(user3.getLastTimeOnlineStatusHasBeenSet().isPresent());
        assertFalse(user4.getLastTimeOnlineStatusHasBeenSet().isPresent());
    }

    @Test
    void addingUserWithIdIgnoredProvidedId() {
        final User user = userService.addUser(new User(1));
        assertTrue(user.getId().isPresent());
        assertEquals(0, (long) user.getId().get());
    }

    @Test
    void additionUserWithCorrectPhoneNumberIsSuccessful() {
        final User userDTO = new User();
        userDTO.setPhoneNumber("89211234567");
        User user = userService.addUser(userDTO);
        assertTrue(user.getId().isPresent());
        userDTO.setPhoneNumber("+79211234567");
        user = userService.addUser(userDTO);
        assertTrue(user.getId().isPresent());
    }

    @Test
    void additionUserWithIncorrectPhoneNumberEndedWithIllegalArgumentException() {
        final User userDTO = new User();
        userDTO.setPhoneNumber("99211234567");
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(userDTO));
        userDTO.setPhoneNumber("79211234567");
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(userDTO));
    }

    @Test
    void additionUserWithCorrectEmailIsSuccessful() {
        final User userDTO = new User();
        userDTO.setEmail("support@tesla.com");
        final User user = userService.addUser(userDTO);
        assertTrue(user.getId().isPresent());
    }

    @Test
    void additionUserWithIncorrectEmailEndedWithIllegalArgumentException() {
        final User userDTO = new User();
        userDTO.setEmail("supporttesla.com");
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(userDTO));
    }

    @Test
    void updateUserWithoutId() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(new User()));
    }

    @Test
    void updateUserWithCorrectPhoneNumberIsSuccessful() {
        Optional<User> user = userService.getUser(1);
        assertTrue(user.isPresent());
        user.get().setPhoneNumber("89211234567");
        userService.update(user.get());
        user = userService.getUser(1);
        assertTrue(user.isPresent());
        assertTrue(user.get().getPhoneNumber().isPresent());
        assertEquals("89211234567", user.get().getPhoneNumber().get());

        user.get().setPhoneNumber("+79211234567");
        userService.update(user.get());
        user = userService.getUser(1);
        assertTrue(user.isPresent());
        assertTrue(user.get().getPhoneNumber().isPresent());
        assertEquals("+79211234567", user.get().getPhoneNumber().get());
    }

    @Test
    void updateUserWithIncorrectPhoneNumberEndedWithIllegalArgumentException() {
        final Optional<User> user = userService.getUser(1);
        assertTrue(user.isPresent());
        user.get().setPhoneNumber("99211234567");
        assertThrows(IllegalArgumentException.class, () -> userService.update(user.get()));
    }

    @Test
    void updateUserWithCorrectEmailIsSuccessful() {
        Optional<User> user = userService.getUser(1);
        assertTrue(user.isPresent());
        user.get().setEmail("support@tesla.com");
        userService.update(user.get());
        user = userService.getUser(1);
        assertTrue(user.isPresent());
        assertTrue(user.get().getEmail().isPresent());
        assertEquals("support@tesla.com", user.get().getEmail().get());
    }

    @Test
    void updateUserWithIncorrectEmailEndedWithIllegalArgumentException() {
        final Optional<User> user = userService.getUser(1);
        assertTrue(user.isPresent());
        user.get().setEmail("supporttesla.com");
        assertThrows(IllegalArgumentException.class, () -> userService.update(user.get()));
    }

    @Test
    void updatingUserWithoutIdEndedWithIllegalArgumentException() {
        final User user = new User();
        assertThrows(IllegalArgumentException.class, () -> userService.update(user));
    }
}