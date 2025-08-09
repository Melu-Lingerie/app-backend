package ru.mellingerie.wishlist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.exceptions.wishlist.WishlistExceptions;
import ru.mellingerie.wishlist.entity.Wishlist;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistClearServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistClearService service;

    @Spy
    private WishlistValidationService validationService = new WishlistValidationService();

    @Test
    void clear_throwsOnInvalidUserId() {
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.clear(null));
        assertThrows(WishlistExceptions.WishListInvalidIdException.class, () -> service.clear(0L));
        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void clear_deletesAll() {
        Long userId = 7L;
        Wishlist w = new Wishlist();
        w.setId(77L);
        w.setUserId(userId);
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));

        service.clear(userId);
        verify(wishlistItemRepository).deleteAllByWishlistId(77L);
    }
}


