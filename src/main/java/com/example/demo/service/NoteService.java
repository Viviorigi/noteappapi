package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    // Lấy tất cả ghi chú của một người dùng
    public List<Note> getAllNotes(User user) {
        return noteRepository.findByUser(user);
    }

    // Tìm kiếm ghi chú theo tiêu đề hoặc nội dung (giới hạn theo user)
    public List<Note> searchNotes(User user, String keyword) {
        List<Note> byTitle = noteRepository.findByUserAndTitleContainingIgnoreCase(user, keyword);
        List<Note> byContent = noteRepository.findByUserAndContentContainingIgnoreCase(user, keyword);
        Set<Note> merged = new LinkedHashSet<>();
        merged.addAll(byTitle);
        merged.addAll(byContent);
        return new ArrayList<>(merged);
    }

    // Lấy 1 ghi chú theo ID, nếu thuộc về user
    public Optional<Note> getNoteById(Long id, User user) {
        return noteRepository.findByIdAndUser(id, user);
    }

    // Tạo ghi chú mới
    public Note createNote(Note note, User user) {
        note.setUser(user);
        note.setCreatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    // Cập nhật ghi chú nếu thuộc về user
    public Optional<Note> updateNote(Long id, Note updatedNote, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            note.setTitle(updatedNote.getTitle());
            note.setContent(updatedNote.getContent());
            note.setColor(updatedNote.getColor());
            note.setCreatedAt(LocalDateTime.now()); // hoặc updatedAt nếu bạn có field đó
            return noteRepository.save(note);
        });
    }

    // Xoá ghi chú nếu thuộc về user
    public boolean deleteNote(Long id, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            noteRepository.delete(note);
            return true;
        }).orElse(false);
    }
}
