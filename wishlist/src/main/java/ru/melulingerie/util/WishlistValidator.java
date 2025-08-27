
package ru.melulingerie.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.exception.AggregatedValidationException;
import ru.melulingerie.exception.ValidationErrors;
import ru.melulingerie.exception.WishlistExceptions;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;

@Slf4j
@Service
public class WishlistValidator {

    public void validatePositiveIdOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw new WishlistExceptions.InvalidIdException(id);
        }
    }

    public void validatePositiveIdOrCollect(Long id, ValidationErrors errors) {
        if (id == null || id <= 0) {
            errors.add("Некорректный идентификатор: " + id);
        }
    }

    public void validateCapacityNotExceededOrCollect(int currentCount, int maxItems, ValidationErrors errors) {
        if (currentCount >= maxItems) {
            errors.add("Превышена максимальная вместимость списка желаний: " + maxItems);
        }
    }

    public void validateDuplicateAbsentOrCollect(boolean duplicateExists, Long productId, Long variantId, ValidationErrors errors) {
        if (duplicateExists) {
            errors.add("Дублирующийся товар в списке желаний (productId=" + productId + ", variantId=" + variantId + ")");
        }
    }

    public void requireOwnedByWishlistOrCollect(WishlistItem item, Long wishlistId, Long itemId, ValidationErrors errors) {
        if (item == null) {
            errors.add("Элемент списка желаний не найден: id=" + itemId);
            return;
        }
        if (item.getWishlist() == null || !item.getWishlist().getId().equals(wishlistId)) {
            errors.add("Элемент id=" + itemId + " не принадлежит списку желаний id=" + wishlistId);
        }
    }

    public void validateWishlistExistsOrCollect(Wishlist wishlist, Long userId, ValidationErrors errors) {
        if (wishlist == null) {
            errors.add("Список желаний пользователя не найден: userId=" + userId);
        }
    }

    public void validateAddToWishlistRequestOrCollect(WishlistAddItemRequestDto request, ValidationErrors errors) {
        if (request == null) {
            errors.add("Запрос на добавление в список желаний не может быть null");
            return;
        }
        validatePositiveIdOrCollect(request.productId(), errors);
        validatePositiveIdOrCollect(request.variantId(), errors);
    }

    public void throwIfHasErrors(ValidationErrors errors) {
        if (errors != null && errors.hasErrors()) {
            throw new AggregatedValidationException(errors.getAll());
        }
    }

    public void validateClearRequest(Long userId, Wishlist wishlist) {
        log.debug("Validating clear wishlist request for userId: {}", userId);
        ValidationErrors errors = new ValidationErrors();

        validatePositiveIdOrCollect(userId, errors);
        validateWishlistExistsOrCollect(wishlist, userId, errors);

        throwIfHasErrors(errors);
    }

    public void validateAddWishlist(Long userId, WishlistAddItemRequestDto request, Wishlist wishlist,
                                    int currentCount, int maxItems, boolean duplicateExists) {
        log.debug("Validating add wishlist item request for userId: {}, request: {}", userId, request);
        ValidationErrors errors = new ValidationErrors();

        validatePositiveIdOrCollect(userId, errors);
        validateAddToWishlistRequestOrCollect(request, errors);
        validateWishlistExistsOrCollect(wishlist, userId, errors);

        if (!errors.hasErrors()) {
            validateCapacityNotExceededOrCollect(currentCount, maxItems, errors);
            validateDuplicateAbsentOrCollect(duplicateExists, request.productId(), request.variantId(), errors);
        }

        throwIfHasErrors(errors);
    }

    public void validateRemoveRequest(Long userId, Long itemId, Wishlist wishlist, WishlistItem item) {
        log.debug("Validating remove wishlist item request for userId: {}, itemId: {}", userId, itemId);
        ValidationErrors errors = new ValidationErrors();

        validatePositiveIdOrCollect(userId, errors);
        validatePositiveIdOrCollect(itemId, errors);
        validateWishlistExistsOrCollect(wishlist, userId, errors);

        if (!errors.hasErrors() && wishlist != null) {
            requireOwnedByWishlistOrCollect(item, wishlist.getId(), itemId, errors);
        }

        throwIfHasErrors(errors);
    }
}