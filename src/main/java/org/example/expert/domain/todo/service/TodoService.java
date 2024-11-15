package org.example.expert.domain.todo.service;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true) // 레벨1_1 코드개선 퀴즈 영속성 컨텍스트를 읽기전용으로 두면 CRUD 중 R만 작동
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(Principal principal, TodoSaveRequest todoSaveRequest) {
        // 레벨 2_9
        // Principal 인터페이스에 가져온 userId로 DB 조회하여 사용자 존재 처리 후 user 객체 사용
        Long userId = Long.valueOf(principal.getName()); // 회원 id 가공
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidRequestException("User not found"));

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickName())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String keyword, String start, String end) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        // 레벨 1_5 날씨 검색값 있을경우는 like 없을경우 일반 전체조회
        if(keyword != null){
            todos = todoRepository.findAllByKeyword(pageable, keyword);
        }

        // 레벨 1_5 날짜 검색값 있을경우 between 없을 경우 일반조회
        if(start != null && end != null){
            todos = todoRepository.findAllByStartAndEnd(pageable, start, end);
        }

        // 레벨 1_5 날씨와 날짜가 동시에 검색될 경우
        if(keyword != null && start != null && end != null) {
            todos = todoRepository.findAllByKeywordAndStartAndEnd(pageable, keyword, start, end);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUserCustom(todoId) // 2_8 QueryDSL로 변경한 커스텀쿼리메서드
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(), user.getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
