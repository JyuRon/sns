package com.example.dto;

import com.example.constant.AlarmType;
import com.example.domain.Alarm;
import com.example.domain.columnDef.AlarmArgs;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AlarmDto {

    private Long id;
    private UserDto user;
    private AlarmType alarmType;
    private AlarmArgs args;
    private LocalDateTime registerAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static AlarmDto fromEntity(Alarm alarm){
        return AlarmDto.of(
                alarm.getId(),
                UserDto.fromEntity(alarm.getUser()),
                alarm.getAlarmType(),
                alarm.getAlarmArgs(),
                alarm.getRegisterAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }

    public static AlarmDto of(Long id, UserDto user, AlarmType alarmType, AlarmArgs alarmArgs, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new AlarmDto(id, user, alarmType, alarmArgs, registerAt, updatedAt, deletedAt);
    }
}
