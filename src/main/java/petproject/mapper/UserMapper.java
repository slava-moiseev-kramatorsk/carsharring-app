package petproject.mapper;

import org.mapstruct.Mapper;
import petproject.config.MapperConfig;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(CreateUserDto createUserDto);
}
