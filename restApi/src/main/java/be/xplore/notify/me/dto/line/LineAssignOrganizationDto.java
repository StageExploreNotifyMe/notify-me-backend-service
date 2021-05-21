package be.xplore.notify.me.dto.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineAssignOrganizationDto {
    private String eventLineId;
    private String organizationId;
}
