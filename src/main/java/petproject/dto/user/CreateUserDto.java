package petproject.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import petproject.validator.user.Email;
import petproject.validator.user.Password;
import petproject.validator.user.UserName;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
public class CreateUserDto {
    @Email
    private String email;
    @UserName
    private String firstName;
    @UserName
    private String lastName;
    @Password
    private String password;
}
