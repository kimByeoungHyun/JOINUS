package com.example.chatservice.service;

import com.example.chatservice.config.security.user.UserDetailsImpl;
import com.example.chatservice.dto.ChatDto.CreateResponse;
import com.example.chatservice.dto.ChatDto.Response;
import com.example.chatservice.dto.ChatRoomDto;
import com.example.chatservice.dto.ChatRoomDto.CreateRequest;
import com.example.chatservice.exception.UserNotMatchException;
import com.example.chatservice.exception.chatroom.ChatRoomNotFoundException;
import com.example.chatservice.feignclient.MainServiceClient;
import com.example.chatservice.feignclient.UserResponseDto;
import com.example.chatservice.model.ChatMessage;
import com.example.chatservice.model.ChatRoom;
import com.example.chatservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MainServiceClient mainServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;


    @Transactional
    public CreateResponse createRoom(@Valid CreateRequest request, UserDetailsImpl userDetails) {
        if (checkDuplicatedForCreate(request, userDetails.getAccount())) {
            ChatRoom room = chatRoomRepository.findByReceiverAndSender(request.getTargetEmail(), userDetails.getAccount());
            return CreateResponse.builder().name(room.getRoomName()).build();
        } else {
            ChatRoom chatRoom = request.toEntity(userDetails.getAccount(),
                    request.getTargetEmail());
            chatRoomRepository.save(chatRoom);
            return CreateResponse.builder().name(chatRoom.getRoomName()).build();
        }
    }

    private boolean checkDuplicatedForCreate(CreateRequest request, String sender) {
        return chatRoomRepository.existsByReceiverAndSender(sender, request.getTargetEmail());
    }

    @Transactional
    public void deleteRoom(String roomName, UserDetailsImpl userDetails) {
        ChatRoom room = getRoom(roomName);
        String email = userDetails.getAccount();

        if (!email.equals(room.getReceiver()) && !email.equals(room.getSender())) {
            throw new UserNotMatchException("본인이 참여중인 채팅방만 삭제할 수 있습니다");
        }
        room.changeStatus();
        chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoomDto.Response getChatRoomDetail(String roomName, String account) {
        ChatRoom room = getRoom(roomName);
        /*채팅 읽음 전환*/
        room.getChats().stream().filter(chat -> !chat.getSender().equals(account))
                .forEach(chatMessage -> chatMessage.setMessageCheckStatus(true));
        chatRoomRepository.save(room);
        /*Sender Receiver 식별*/
        String profileImg = "";

        /*Response Dto*/
        UserResponseDto userResponseDto = getUserInfo(room,account);
        log.info("value:{}", userResponseDto);
        return ChatRoomDto.Response.builder()
                .room(room)
                .chats(chatConverToResponseDto(room.getChats()))
                .userResponseDto(userResponseDto)
                .build();
    }

    /*user정보 feignclient*/
    private UserResponseDto getUserInfo(ChatRoom room, String account) {
        /*CircuitBreaker*/
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        UserResponseDto userResponseDto = new UserResponseDto();
        if (account.equals(room.getReceiver())) {
            userResponseDto = circuitBreaker.run(() -> mainServiceClient.getInfo("true", room.getSender()), throwable -> new UserResponseDto());
        } else {
            userResponseDto = circuitBreaker.run(() -> mainServiceClient.getInfo("true", room.getReceiver()), throwable -> new UserResponseDto());
        }
        return userResponseDto;
    }

    private List<Response> chatConverToResponseDto(List<ChatMessage> chats) {
        List<Response> responseList = new ArrayList<>();
        for (ChatMessage chat : chats) {
            Response response = Response.builder()
                    .chat(chat)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }

    @Transactional(readOnly = true)
    public Page<ChatRoomDto.Response> getChatRoomList(String email, Pageable pageable) {
        Page<ChatRoom> rooms = new PageImpl<>(Collections.emptyList());
        rooms = chatRoomRepository.findAllByEmail(email, pageable);
        return new PageImpl<>(entityToListDto(rooms, email), pageable, rooms.getTotalElements());
    }

    private List<ChatRoomDto.Response> entityToListDto(Page<ChatRoom> rooms, String email) {
        return rooms.stream().map(room -> ChatRoomDto.Response.builder()
                        .room(room)
                        .unreadMessageCount(getUnreadCount(room))
                        .latestChatMessage(getLatestChatMessage(room))
                        .userResponseDto(getUserInfo(room,email))
                        .build())
                .collect(Collectors.toList());
    }

    private String getReceiverEmail(ChatRoom room, String email) {
        String nickname = "";
        if (room.getReceiver().equals(email)) {
            return room.getSender();
        } else return room.getReceiver();
    }

    private String getReceiverNickname(ChatRoom room, String email) {
        String nickname = "";
        if (room.getReceiver().equals(email)) {
            return room.getSender();
        } else return room.getReceiver();
    }

    private String getLatestChatMessage(ChatRoom room) {
        Optional<ChatMessage> message = room.getChats().stream()
                .reduce((o1, o2) -> o1.getCreatedAt().isAfter(o2.getCreatedAt()) ? o1 : o2);
        return message.map(ChatMessage::getMessage).orElse(null);
    }

    private Long getUnreadCount(ChatRoom room) {
        return room.getChats().stream().filter(chat -> !chat.isMessageCheckStatus())
                .count();
    }

    private ChatRoom getRoom(String roomName) {
        return chatRoomRepository.findChatRoomByRoomName(roomName)
                .orElseThrow(() -> new ChatRoomNotFoundException("존재하지않는 채팅방입니다."));

    }
}
