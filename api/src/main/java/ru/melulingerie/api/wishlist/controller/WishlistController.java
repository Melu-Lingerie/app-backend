package ru.melulingerie.api.wishlist.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.wishlist.resource.WishlistResource;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.service.WishlistAddItemFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistClearFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistGetFacadeService;
import ru.melulingerie.facade.wishlist.service.WishlistRemoveItemFacadeService;

import java.awt.print.Pageable;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishlistController implements WishlistResource {

    private final WishlistGetFacadeService wishlistGetFacadeService;
    private final WishlistClearFacadeService wishlistClearFacadeService;
    private final WishlistAddItemFacadeService wishlistAddItemFacadeService;
    private final WishlistRemoveItemFacadeService wishlistRemoveItemFacadeService;

    @Override
    public ResponseEntity<WishlistGetFacadeResponseDto> getWishlist(Long wishlistId) {
        WishlistGetFacadeResponseDto response = wishlistGetFacadeService.getWishlist(wishlistId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WishlistAddFacadeResponseDto> addItemToWishlist(Long wishlistId, WishlistAddFacadeRequestDto request) {
        WishlistAddFacadeResponseDto response = wishlistAddItemFacadeService.addItemToWishlist(wishlistId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> removeItemsFromWishlist(Long wishlistId, List<Long> itemId) {
        wishlistRemoveItemFacadeService.removeItemsFromWishlist(wishlistId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Integer> clearWishlist(Long wishlistId) {
        return ResponseEntity.ok(wishlistClearFacadeService.clearWishlist(wishlistId));
    }
}