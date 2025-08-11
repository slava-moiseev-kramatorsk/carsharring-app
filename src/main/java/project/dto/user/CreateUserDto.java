package project.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import project.validator.user.Email;
import project.validator.user.Password;
import project.validator.user.UserName;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
public class CreateUserDto {
    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @UserName
    @NotBlank(message = "FirstName cannot be empty")
    private String firstName;
    @UserName
    @NotBlank(message = "LastName cannot be empty")
    private String lastName;
    @Password
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
