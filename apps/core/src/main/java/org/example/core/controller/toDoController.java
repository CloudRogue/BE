package org.example.core.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.response.toDoListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class toDoController {

    private final toDoService toDoService;

    // 1. 나의 to-do 목록 조회
    @GetMapping
    public ResponseEntity<toDoListResponse> getToDoList() {
        toDoListResponse response = toDoService.getToDoList();
        return ResponseEntity.ok(response);
    }

    // 2. to-do 생성
    @PostMapping
    public ResponseEntity<Void> createToDo() {
        toDoService.createToDo();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // 3. 날짜로 to-do 조회
    @GetMapping
    public ResponseEntity<toDoListResponse> getToDoByDate() {
        toDoListResponse response = toDoService.getToDoByDate();
        return ResponseEntity.ok(response);
    }
    // 4. to-do 수정
    @PatchMapping
    public ResponseEntity<Void> updateToDo() {
        toDoService.updateToDo();
        return ResponseEntity.ok().build();
    }
    // 5. to-do 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteToDo() {
        toDoService.deleteToDo();
        return ResponseEntity.noContent().build();
    }
    // 6. to-do 완료 표시
    @PostMapping
    public ResponseEntity<Void> completeToDo() {
        toDoService.completeToDo();
        return ResponseEntity.ok().build();
    }
    // 7. to-do 미완료 표시or 완료 취소
    @PostMapping
    public ResponseEntity<Void> uncompleteToDo() {
        toDoService.uncompleteToDo();
        return ResponseEntity.ok().build();
    }
}
