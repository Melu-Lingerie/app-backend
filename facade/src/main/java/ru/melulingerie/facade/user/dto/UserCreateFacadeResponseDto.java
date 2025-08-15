package ru.melulingerie.facade.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeResponseDto {//TODO мб рекорд?

    private Long userId;
    private Long cartId;
    private Long wishlistId;
}
