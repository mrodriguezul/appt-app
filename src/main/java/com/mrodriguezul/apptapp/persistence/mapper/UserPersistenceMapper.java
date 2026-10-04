package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Role;
import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.persistence.entity.UserEntity;
import com.mrodriguezul.apptapp.persistence.entity.UserRoleEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToDomain")
    User toUsuario(UserEntity userEntity);

    List<User> toUsuarios(List<UserEntity> userEntities);

    @InheritInverseConfiguration
    @Mapping(target = "roles", ignore = true)
    UserEntity toUserEntity(User user);

    List<UserEntity> toUserEntities(List<User> users);

    @Named("mapRolesToDomain")
    default List<Role> mapRolesToDomain(List<UserRoleEntity> roleEntities) {
        if (roleEntities == null) {
            return null;
        }
        return roleEntities.stream()
                .map(this::mapSingleRole)
                .collect(Collectors.toList());
    }

    default Role mapSingleRole(UserRoleEntity roleEntity) {
        if (roleEntity == null) {
            return null;
        }
        Role role = new Role();
        role.setName(roleEntity.getRole());
        role.setGrantedDate(roleEntity.getGrantedDate());
        return role;
    }
}
