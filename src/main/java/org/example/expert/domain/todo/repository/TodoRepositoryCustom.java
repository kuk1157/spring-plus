package org.example.expert.domain.todo.repository;

import java.util.Optional;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUserCustom(Long todoId);
}
