package com.konkuk.gp.core.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message<T> {
    public static final String SENDER_SERVER = "SERVER";
    public static final String SENDER_AI = "AI";
    public static final String SENDER_CLIENT = "CLIENT";

    public static final String STATUS_OK = "SUCCESS";
    public static final String STATUS_NOT_OK = "FAILED";


    // TODO : enum
    private String sender;
    private String status;
    private T data;

    public static <G> Message<G> of(G data, String status) {
        return new Message<>(SENDER_SERVER, status, data);
    }
}
