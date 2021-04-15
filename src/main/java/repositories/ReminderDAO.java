package repositories;

import entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
public interface ReminderDAO extends JpaRepository<Reminder, Long> {

    @Transactional
    @Query("SELECT r FROM Reminder r JOIN FETCH r.user WHERE r.actual = true AND r.actionDate between :startDateTime AND :endDateTime")
    List<Reminder> findActualRemindFromTo(@Param("startDateTime") LocalDateTime startDateTime,
                                     @Param("endDateTime") LocalDateTime endDateTime);
}
