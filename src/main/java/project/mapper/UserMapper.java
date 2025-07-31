package project.mapper;

import org.mapstruct.Mapper;
import project.config.MapperConfig;
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(CreateUserDto createUserDto);
}
