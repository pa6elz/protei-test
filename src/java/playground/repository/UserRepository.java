package playground.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import playground.domain.user.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
