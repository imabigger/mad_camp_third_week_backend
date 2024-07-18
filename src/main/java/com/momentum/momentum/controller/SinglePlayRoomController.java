package com.momentum.momentum.controller;

import com.momentum.momentum.entity.GameSettings;
import com.momentum.momentum.entity.SRoom;
import com.momentum.momentum.model.CreateRoomRequest;
import com.momentum.momentum.service.SinglePlayRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/s-rooms")
public class SinglePlayRoomController {
    private final SinglePlayRoomService roomService;

    @Autowired
    public SinglePlayRoomController(SinglePlayRoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<SRoom> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/user")
    public List<SRoom> getAllRoomsByUserId(Authentication authentication) {
        String userId = authentication.getName();
        return roomService.getRoomsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SRoom> getRoomById(@PathVariable String id) {
        Optional<SRoom> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<SRoom> createRoom(@RequestBody CreateRoomRequest createRoomRequest, Authentication authentication) {
        System.out.println("[CreateRoomRequest] start");
        try {
            String userId = authentication.getName();
            SRoom SRoom = new SRoom();
            SRoom.setUserId(userId);
            SRoom.setRoom(createRoomRequest.getRoom());
            SRoom.setScore(0);

            GameSettings settings = new GameSettings();
            settings.setTheme(createRoomRequest.getTheme());
            settings.setKeywords(createRoomRequest.getKeywords());
            SRoom.setSettings(settings);

            SRoom.setItems(createRoomRequest.getItems());
            return ResponseEntity.ok(roomService.createRoom(SRoom, createRoomRequest.getUserStats()));
        } catch (Exception e) {
            System.out.println("[CreateRoomRequest] error" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SRoom> updateRoom(@PathVariable String id, @RequestBody SRoom SRoom) {
        try {
            SRoom updatedSRoom = roomService.updateRoom(id, SRoom);
            return ResponseEntity.ok(updatedSRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted successfully"));
    }
}
