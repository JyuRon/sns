package com.example.dto.response;

import com.example.constant.AlarmType;
import com.example.domain.columnDef.AlarmArgs;
import com.example.dto.AlarmDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AlarmResponse {
    private Long id;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private LocalDateTime registerAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static AlarmResponse of(Long id, AlarmType alarmType, AlarmArgs alarmArgs, String text, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new AlarmResponse(id, alarmType, alarmArgs, text, registerAt, updatedAt, deletedAt);
    }

    public static AlarmResponse fromDto(AlarmDto alarmDto){
        return AlarmResponse.of(
                alarmDto.getId(),
                alarmDto.getAlarmType(),
                alarmDto.getArgs(),
                alarmDto.getAlarmType().getAlarmText(),
                alarmDto.getRegisterAt(),
                alarmDto.getUpdatedAt(),
                alarmDto.getDeletedAt()
        );
    }

}
