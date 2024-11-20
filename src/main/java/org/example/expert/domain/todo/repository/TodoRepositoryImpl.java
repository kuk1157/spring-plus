package org.example.expert.domain.todo.repository;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearch;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    // 3-10 QueryDSL 검색
    @Override
    public Page<TodoSearch> findTodoWithCommentAndManagerCounts(Pageable pageable, String title, String start, String end, String nickName){
        QTodo todo = QTodo.todo;
        QComment comment = QComment.comment;
        QManager manager = QManager.manager;
        QUser user = QUser.user;

        // String -> LocalDateTime 으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(start + " 00:00:00", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end + " 23:59:59", formatter);


        JPAQuery<TodoSearch> query = queryFactory
            .select(Projections.constructor(TodoSearch.class,
                todo.id,
                todo.title,
                comment.id.count().as("commentCount"),
                manager.id.count().as("managerCount")))
            .from(todo)
            .leftJoin(comment).on(comment.todo.id.eq(todo.id))
            .leftJoin(manager).on(manager.todo.id.eq(todo.id))
            .where(todo.title.contains(title)
                .and(todo.user.id.eq(
                    JPAExpressions.select(user.id)
                        .from(user)
                        .where(user.nickName.contains(nickName))
                ))
                .and(todo.createdAt.between(startDateTime, endDateTime))
        )
        .groupBy(todo.id, todo.title)
        .orderBy(todo.createdAt.desc());

        // 페이징 처리
        long total = query.fetchCount(); // 전체 건수
        List<TodoSearch> results = query
            .offset(pageable.getOffset()) // 페이지 오프셋
            .limit(pageable.getPageSize()) // 페이지 크기
            .fetch(); // 결과 리스트

        return new PageImpl<>(results, pageable, total); // PageImpl을 사용하여 반환
    }
}
