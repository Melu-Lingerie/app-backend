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
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistItemRemoveServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistItemRemoveService service;

    @Spy
    private WishlistValidationService validationService = new WishlistValidationService();

    @Test
    void remove_throwsOnInvalidIds() {
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.remove(null, 1L));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.remove(1L, null));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.remove(0L, 1L));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.remove(1L, 0L));
        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void remove_deletes_whenOwned() {
        Long userId = 2L;
        Long itemId = 3L;
        Wishlist w = new Wishlist();
        w.setId(10L);
        w.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));

        WishlistItem wi = new WishlistItem();
        wi.setId(itemId);
        wi.setWishlist(w);
        when(wishlistItemRepository.findById(itemId)).thenReturn(Optional.of(wi));

        service.remove(userId, itemId);
        verify(wishlistItemRepository).delete(wi);
    }

    @Test
    void remove_throwsWhenNotOwnedOrMissing() {
        Long userId = 2L;
        Long itemId = 3L;
        Wishlist w = new Wishlist();
        w.setId(10L);
        w.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));

        when(wishlistItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(WishlistExceptions.WishlistItemNotFoundException.class, () -> service.remove(userId, itemId));
    }
}


