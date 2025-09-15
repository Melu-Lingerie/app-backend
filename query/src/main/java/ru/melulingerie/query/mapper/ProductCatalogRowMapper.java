package ru.melulingerie.query.mapper;


import org.springframework.jdbc.core.RowMapper;
import ru.melulingerie.query.dto.response.ProductCatalogItemResponseDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductCatalogRowMapper implements RowMapper<ProductCatalogItemResponseDto> {
    @Override
    public ProductCatalogItemResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long productId = rs.getLong("product_id");
        String name = rs.getString("name");
        BigDecimal price = rs.getBigDecimal("price");
        String s3url = rs.getString("s3url");
        return new ProductCatalogItemResponseDto(productId, name, price, s3url);
    }
}
