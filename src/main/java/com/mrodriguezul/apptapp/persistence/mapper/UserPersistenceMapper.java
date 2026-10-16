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
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToDomain")
    User toModel(UserEntity userEntity);

    @InheritInverseConfiguration
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(User user);

    @Named("mapRolesToDomain")
    default List<Role> mapRolesToDomain(List<UserRoleEntity> roleEntities) {
        return Optional.ofNullable(roleEntities)
                .map(roles -> roles.stream()
                        .map(this::mapSingleRole)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    @Named("mapSingleRole")
    default Role mapSingleRole(UserRoleEntity roleEntity) {
        return Optional.ofNullable(roleEntity)
                .map(r -> {
                    Role role = new Role();
                    role.setName(r.getRole());
                    role.setGrantedDate(r.getGrantedDate());
                    return role;
                })
                .orElse(null);
    }
}
