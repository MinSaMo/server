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

    // TODO : enum
    private String sender;
    private T data;

    public static <G> Message<G> of(G data) {
        return new Message<>(SENDER_SERVER, data);
    }
}
