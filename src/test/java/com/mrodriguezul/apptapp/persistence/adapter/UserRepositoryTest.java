package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.persistence.crud.UserCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.UserEntity;
import com.mrodriguezul.apptapp.persistence.mapper.UserPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    @Mock
    private UserCrudRepository userCrudRepository;
    @Mock
    private UserPersistenceMapper mapper;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(userCrudRepository, mapper);
    }

    @Test
    void findByUsername_shouldMapExistingEntity() {
        UserEntity entity = new UserEntity();
        User user = new User("alice");
        when(userCrudRepository.findById("alice")).thenReturn(Optional.of(entity));
        when(mapper.toModel(entity)).thenReturn(user);

        assertEquals(Optional.of(user), userRepository.findByUsername("alice"));
        verify(mapper).toModel(entity);
    }

    @Test
    void findByUsername_shouldReturnEmptyWhenEntityDoesNotExist() {
        when(userCrudRepository.findById("missing")).thenReturn(Optional.empty());

        assertTrue(userRepository.findByUsername("missing").isEmpty());
        verifyNoInteractions(mapper);
    }
}
