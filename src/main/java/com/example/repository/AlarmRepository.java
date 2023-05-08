package com.example.repository;

import com.example.domain.Alarm;
import com.example.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByUser(UserAccount userAccount, Pageable pageable);
}
