package ru.melulingerie.api.wishlist.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.wishlist.resource.WishlistResource;
import ru.melulingerie.facade.wishlist.dto.GetWishlistListItemsResponseDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistRequestDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistResponseDto;
import ru.melulingerie.facade.wishlist.service.WishlistQueryFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistItemAddFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistItemRemoveFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistClearFacadeService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishlistController implements WishlistResource {

    private final WishlistQueryFacadeService wishlistQueryFacadeService;
    private final WishlistItemAddFacadeService wishlistItemAddFacadeService;
    private final WishlistItemRemoveFacadeService wishlistItemRemoveFacadeService;
    private final WishlistClearFacadeService wishlistClearFacadeService;

    @Override
    public ResponseEntity<GetWishlistListItemsResponseDto> getWishlist(Long userId) {
        GetWishlistListItemsResponseDto response = wishlistQueryFacadeService.getWishlist(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AddWishlistResponseDto> addItemToWishlist(Long userId, AddWishlistRequestDto request) {
        AddWishlistResponseDto response = wishlistItemAddFacadeService.addItemToWishlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> removeItemFromWishlist(Long userId, Long itemId) {
        wishlistItemRemoveFacadeService.removeItemFromWishlist(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> clearWishlist(Long userId) {
        wishlistClearFacadeService.clearWishlist(userId);
        return ResponseEntity.noContent().build();
    }
}
