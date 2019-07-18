package com.github.alex353cay.protei.test.repository;

import com.github.alex353cay.protei.test.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
