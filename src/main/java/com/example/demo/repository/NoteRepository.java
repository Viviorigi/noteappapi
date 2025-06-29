package com.example.demo.repository;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);

    // Tìm kiếm cả theo tiêu đề hoặc nội dung, giới hạn theo user
    List<Note> findByUserAndTitleContainingIgnoreCase(User user, String title);

    List<Note> findByUserAndContentContainingIgnoreCase(User user, String content);

    Optional<Note> findByIdAndUser(Long id, User user);
}
