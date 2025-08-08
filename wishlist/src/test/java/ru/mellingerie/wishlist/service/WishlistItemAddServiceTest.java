package ru.mellingerie.wishlist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Spy;
import ru.mellingerie.wishlist.entity.Wishlist;
import ru.mellingerie.wishlist.entity.WishlistItem;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistCapacityExceededException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemDuplicateException;
import ru.mellingerie.wishlist.model.AddToWishlistModel;
import ru.mellingerie.wishlist.model.AddToWishlistResponseModel;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistItemAddServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistItemAddService service;

    @Spy
    private WishlistValidationService validationService = new WishlistValidationService();

    @Test
    void add_throwsOnInvalidUserId() {
        assertThrows(InvalidIdException.class, () -> service.add(0L, new AddToWishlistModel(1L, 1L)));
        assertThrows(InvalidIdException.class, () -> service.add(null, new AddToWishlistModel(1L, 1L)));
        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void add_createsWishlist_whenNotExists() {
        Long userId = 5L;
        // For add, service requires existing wishlist now -> throw InvalidId

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(InvalidIdException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
    }

    @Test
    void add_throwsOnCapacityExceeded() {
        Long userId = 5L;
        Wishlist w = new Wishlist();
        w.setId(10L);
        w.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));
        when(wishlistItemRepository.findAllByWishlistId(10L)).thenReturn(
                java.util.Collections.nCopies(200, new WishlistItem()));

        assertThrows(WishlistCapacityExceededException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
    }

    @Test
    void add_throwsOnDuplicate() {
        Long userId = 5L;
        Wishlist w = new Wishlist();
        w.setId(10L);
        w.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));
        when(wishlistItemRepository.findAllByWishlistId(10L)).thenReturn(List.of());
        when(wishlistItemRepository.findDuplicate(10L, 1L, 2L)).thenReturn(Optional.of(new WishlistItem()));

        assertThrows(WishlistItemDuplicateException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
    }
}


