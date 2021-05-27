package be.xplore.notify.me.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    private String firstname;
    private String lastname;
    private String email;
    private String mobileNumber;
    private String password;
}
