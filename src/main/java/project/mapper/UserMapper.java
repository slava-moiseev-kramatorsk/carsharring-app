package project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import project.config.MapperConfig;
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(CreateUserDto createUserDto);

    void updateUserFromDto(CreateUserDto dto, @MappingTarget User user);
}
