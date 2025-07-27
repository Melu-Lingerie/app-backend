package ru.melulingerie.entity;

import lombok.Data;
import java.util.List;
//TODO примерное наполнение 
@Data
public class MarketingTags {
    private String source; // utm_source, канал привлечения
    private String segment; // сегмент пользователя (VIP, New, etc.)
    private List<String> interests; // интересы (категории товаров)
    private List<String> favoriteBrands; // любимые бренды
    private List<String> favoriteColors; // любимые цвета
    private boolean loyaltyProgram; // участвует ли в программе лояльности
    private String region; // регион пользователя
    private String city; // город пользователя
    private String lastPurchase; // дата последней покупки (ISO-строка)
}