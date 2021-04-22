package be.xplore.notify.me.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationProcessDto {
    private String UserOrganizationId;
    private boolean Accepted;
}
