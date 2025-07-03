package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.service.NoteService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    // Lấy tất cả ghi chú của người dùng
    @GetMapping(produces = "application/json; charset=UTF-8")
    public List<Note> getAllNotes(@RequestParam(required = false) String search, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        if (search != null && !search.isEmpty()) {
            return noteService.searchNotes(user, search);
        }
        return noteService.getAllNotes(user);
    }

    // Lấy ghi chú theo id nếu thuộc về user
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return noteService.getNoteById(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo ghi chú mới
    @PostMapping
    public Note createNote(@RequestBody Note note, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return noteService.createNote(note, user);
    }

    // Cập nhật ghi chú nếu thuộc về user
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return noteService.updateNote(id, note, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Xoá ghi chú nếu thuộc về user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        boolean deleted = noteService.deleteNote(id, user);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/toggle-pin")
    public ResponseEntity<?> togglePin(@PathVariable Long id, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Note updated = noteService.togglePin(id, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
