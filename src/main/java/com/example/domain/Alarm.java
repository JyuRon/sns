package com.example.domain;

import com.example.constant.AlarmType;
import com.example.domain.columnDef.AlarmArgs;
import com.example.domain.converter.AlarmTypeConverter;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(
        name = "\"alarm\"",
        indexes = {
                @Index(name = "user_id_index", columnList = "userAccount_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE \"alarm\" set deleted_at = now() where id=?")
@Where(clause = "deleted_at is null")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 알람 수신자의 정보
    @ManyToOne
    @JoinColumn(name = "userAccount_id")
    private UserAccount user;

    @Convert(converter = AlarmTypeConverter.class)
    private AlarmType alarmType;

    // psql 의 경우 json 을 압축하는 기능을 지원함과 동시에 index 까지 지원을 한다.가
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AlarmArgs alarmArgs;



    public Alarm(Long id, UserAccount user, AlarmType alarmType, AlarmArgs alarmArgs) {
        this.id = id;
        this.user = user;
        this.alarmType = alarmType;
        this.alarmArgs = alarmArgs;
    }

    public static Alarm of(Long id, UserAccount user, AlarmType alarmType, AlarmArgs alarmArgs){
        return new Alarm(id, user, alarmType, alarmArgs);
    }

    public static Alarm of(UserAccount user, AlarmType alarmType, AlarmArgs alarmArgs){
        return Alarm.of(null, user, alarmType, alarmArgs);
    }
}
