package org.example.expert.domain.todo.dto.response;
import lombok.Getter;

@Getter
public class TodoSearch {
    private final Long id; // 일정 고유번호
    private final String title; // 일정 제목
    private final Long mangerCount; // 일정의 담당자 수
    private final Long commentCount; // 일정의 댓글 수

    public TodoSearch(Long id, String title, Long mangerCount, Long commentCount) {
        this.id = id;
        this.title = title;
        this.mangerCount = mangerCount;
        this.commentCount = commentCount;
    }
}

