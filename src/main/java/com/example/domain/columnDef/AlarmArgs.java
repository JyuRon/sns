package com.example.domain.columnDef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmArgs {

    // 알람을 발생 시킨 사람
    private Long fromUserId;

    // post or comment id
    private Long targetId;

    public static AlarmArgs of(Long fromUserId, Long targetId){
        return new AlarmArgs(fromUserId, targetId);
    }
}
