package com.example.chatservice.tests.kafkatest;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestPayload {
//    private Long          test_id;
    private String         title;
    private String         content;


}