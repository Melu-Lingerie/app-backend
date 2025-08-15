package ru.mellingerie.users.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponseDto {
    //TODO оставить только userId
    private Long userId;
    private Long cartId;
    private Long wishlistId;
}
