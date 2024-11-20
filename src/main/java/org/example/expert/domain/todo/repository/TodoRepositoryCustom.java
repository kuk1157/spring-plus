package org.example.expert.domain.todo.repository;

import java.util.Optional;
import org.example.expert.domain.todo.dto.response.TodoSearch;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUserCustom(Long todoId);

    // 3-10 QueryDSL 검색
    Page<TodoSearch> findTodoWithCommentAndManagerCounts(Pageable pageable, String title, String start, String end, String nickName);
}
