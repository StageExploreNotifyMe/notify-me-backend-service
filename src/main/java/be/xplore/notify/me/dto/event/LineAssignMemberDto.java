package be.xplore.notify.me.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineAssignMemberDto {
    private String eventLineId;
    private String memberId;
}
