package be.xplore.notify.me.dto.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineAssignEventDto {
    private String lineId;
    private String eventId;
    private String lineManagerId;
}
