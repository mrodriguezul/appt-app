package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.domain.repository.IUserRepository;
import com.mrodriguezul.apptapp.persistence.crud.UserCrudRepository;
import com.mrodriguezul.apptapp.persistence.mapper.UserPersistenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {
    private final UserCrudRepository userCrudRepository;
    private final UserPersistenceMapper mapper;

    @Autowired
    public UserRepository(UserCrudRepository userCrudRepository, UserPersistenceMapper mapper) {
        this.userCrudRepository = userCrudRepository;
        this.mapper = mapper;
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return userCrudRepository.findById(username).map(mapper::toModel);
    }
}
