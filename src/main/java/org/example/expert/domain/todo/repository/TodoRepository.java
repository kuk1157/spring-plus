package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 기존 페이징 쿼리
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // 레벨 1_5 날씨검색 쿼리
    String keyword = "WHERE weather LIKE %:weather%";
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u "+keyword+" ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByKeyword(Pageable pageable, String weather);

    // 레벨 1_5 날짜검색 쿼리
    String date = "WHERE DATE_FORMAT(t.modifiedAt,'%Y-%m-%d') BETWEEN :start AND :end";
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u "+date+" ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByStartAndEnd(Pageable pageable, String start, String end);

    // 레벨 1_5 날씨+날짜 검색 쿼리
    String finalQuery = "WHERE weather LIKE %:weather% AND DATE_FORMAT(t.modifiedAt,'%Y-%m-%d') BETWEEN :start AND :end";
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u "+finalQuery+" ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByKeywordAndStartAndEnd(Pageable pageable, String weather, String start, String end);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
