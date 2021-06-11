package be.xplore.notify.me.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationCodeDto {
    String userId;
    String emailCode;
    String smsCode;

}
