package com.example.studytask.repository;

import com.example.studytask.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    @Query("""
            select record from StudyRecord record
            join fetch record.task task
            where (:taskId is null or task.id = :taskId)
              and (:startDate is null or record.studyDate >= :startDate)
              and (:endDate is null or record.studyDate <= :endDate)
            order by record.studyDate desc, record.id desc
            """)
    List<StudyRecord> search(
            @Param("taskId") Long taskId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select record from StudyRecord record
            join fetch record.task
            where record.studyDate between :startDate and :endDate
            order by record.studyDate asc, record.id asc
            """)
    List<StudyRecord> findForStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select coalesce(sum(record.durationMinutes), 0)
            from StudyRecord record
            where record.studyDate between :startDate and :endDate
            """)
    Long sumDurationBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("delete from StudyRecord record where record.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);
}
