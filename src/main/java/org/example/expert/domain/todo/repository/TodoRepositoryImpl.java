package org.example.expert.domain.todo.repository;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUserCustom(Long todoId){
        return Optional.ofNullable(queryFactory
            .selectFrom(todo)
            .join(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne());
    }

}
