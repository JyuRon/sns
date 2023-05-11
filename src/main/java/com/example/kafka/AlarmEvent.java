package com.example.kafka;

import com.example.constant.AlarmType;
import com.example.domain.columnDef.AlarmArgs;
import com.example.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AlarmEvent {
    private Long receiveUserID;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
}
