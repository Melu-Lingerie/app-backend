package ru.mellingerie.wishlist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.exceptions.wishlist.WishlistExceptions;
import ru.mellingerie.wishlist.entity.Wishlist;
import ru.mellingerie.wishlist.entity.WishlistItem;
import ru.mellingerie.wishlist.model.AddToWishlistModel;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.add(0L, new AddToWishlistModel(1L, 1L)));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.add(null, new AddToWishlistModel(1L, 1L)));
        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void add_createsWishlist_whenNotExists() {
        Long userId = 5L;
        // For add, service requires existing wishlist now -> throw InvalidId

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
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

        assertThrows(WishlistExceptions.WishlistCapacityExceededException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
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

        assertThrows(WishlistExceptions.WishlistItemDuplicateException.class, () -> service.add(userId, new AddToWishlistModel(1L, 2L)));
    }
}


