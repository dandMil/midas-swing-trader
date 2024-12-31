package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.WatchlistEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;

public interface WatchListRepository extends JpaRepository<WatchlistEntry,String> {
    @Transactional
    void deleteByName(String name);



//    @Query(value = """
//    SELECT name
//    FROM watch_list
//    WHERE date_created >= :startDate
//    GROUP BY name
//    HAVING COUNT(id) >= 2
//""", nativeQuery = true)
//    List<String> findNamesWithAtLeastTwoEntriesInPeriod(@Param("startDate") String startDate);

//    @Query(value = """
//    SELECT name
//    FROM watch_list
//    WHERE date(date_created) >= :startDate
//    GROUP BY name
//    HAVING COUNT(id) >= 2
//""", nativeQuery = true)
//    List<String> findNamesWithAtLeastTwoEntriesInPeriod(@Param("startDate") LocalDate startDate);

    @Query(value = """
    SELECT *
    FROM watch_list w
    WHERE date(w.date_created) >= :startDate
    AND (
        SELECT COUNT(*) 
        FROM watch_list 
        WHERE name = w.name 
        AND date(date_created) >= :startDate
    ) >= 2 and movement = 'gainers' order by date_created desc
""", nativeQuery = true)
    List<WatchlistEntry> findRecordsWithAtLeastTwoEntriesInPeriod(@Param("startDate") LocalDate startDate);

}




