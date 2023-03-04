package com.example.chatservice.dto;

import com.example.chatservice.model.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
public class ChatDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class CreateRequest {

        //roomId
        @NotNull
        private Long roomId;
        //채팅 내용
        @NotBlank
        private String message;

        private String sender;

    }

    @Getter
    public static class CreateResponse {

        private String roomName;
        @Builder
        CreateResponse(String name){
            this.roomName =name;
        }
    }

    @Getter
    public static class Response {
        //채팅 Id
        private Long id;

        //채팅 작성자
        private String sender;

        private String message;

        private LocalDateTime createAt;

        private LocalDateTime updatedAt;

        @Builder Response(ChatMessage chat){
            this.id = chat.getId();
            this.message = chat.getMessage();
            this.sender = chat.getSender();
            this.createAt = chat.getCreatedAt();
            this.updatedAt = chat.getUpdatedAt();
        }
    }
}
