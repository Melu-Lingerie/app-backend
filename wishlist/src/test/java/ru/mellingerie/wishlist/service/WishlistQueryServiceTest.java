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
import ru.mellingerie.wishlist.model.WishlistModel;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistQueryServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistQueryService service;

    @Spy
    private WishlistValidationService validationService = new WishlistValidationService();

    @Test
    void getWishlist_throwsOnInvalidUserId() {
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.getWishlist(0L));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.getWishlist(null));
        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void getWishlist_returnsItems_whenWishlistExists() {
        Long userId = 10L;
        Wishlist wishlist = new Wishlist();
        wishlist.setId(100L);
        wishlist.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(wishlist));

        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setWishlist(wishlist);
        item.setProductId(111L);
        item.setVariantId(222L);
        item.setAddedAt(LocalDateTime.now());
        when(wishlistItemRepository.findAllByWishlistId(100L)).thenReturn(List.of(item));

        WishlistModel result = service.getWishlist(userId);

        assertNotNull(result);
        assertEquals(1, result.itemsCount());
        assertEquals(1, result.items().size());
        assertEquals(111L, result.items().getFirst().productId());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void getWishlist_createsWishlist_whenNotExists() {
        Long userId = 10L;
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Wishlist created = new Wishlist();
        created.setId(200L);
        created.setUserId(userId);
        // no save should be invoked; no item fetch since wishlistId is null

        WishlistModel result = service.getWishlist(userId);
        assertNotNull(result);
        assertEquals(0, result.itemsCount());
        verify(wishlistRepository, never()).save(any(Wishlist.class));
        verifyNoInteractions(wishlistItemRepository);
    }
}


