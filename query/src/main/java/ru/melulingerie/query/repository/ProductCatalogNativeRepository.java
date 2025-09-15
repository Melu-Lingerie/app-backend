package ru.melulingerie.query.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;
import ru.melulingerie.query.dto.response.ProductCatalogItemResponseDto;
import ru.melulingerie.query.mapper.ProductCatalogRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductCatalogNativeRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ProductCatalogNativeRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Page<ProductCatalogItemResponseDto> findCatalog(
            ProductCatalogFilterRequestDto req,
            Pageable pageable
    ) {

        StringBuilder select = new StringBuilder("""
            select
              p.id           as product_id,
              p.name         as name,
              pr.base_amount as price,
              m.s3url       as s3url
            from products p
            join prices pr on pr.id = p.price_id
            left join media m on m.id = p.main_media_id
            """); // фиксированные идентификаторы таблиц/колонок, не из ввода [3][4][5]

        StringBuilder where = new StringBuilder(" where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (req != null && StringUtils.hasText(req.name())) {
            where.append(" and p.name ilike :name ");
            params.addValue("name", "%" + req.name().trim() + "%"); // биндинг строки [23]
        }
        if (req != null && req.minPrice() != null) {
            where.append(" and pr.base_amount >= :minPrice ");
            params.addValue("minPrice", req.minPrice()); // биндинг BigDecimal [23]
        }
        if (req != null && req.maxPrice() != null) {
            where.append(" and pr.base_amount <= :maxPrice ");
            params.addValue("maxPrice", req.maxPrice()); // биндинг BigDecimal [23]
        }
        if (req != null && req.categories() != null && !req.categories().isEmpty()) {
            where.append(" and p.category_id in (:categories) ");
            params.addValue("categories", req.categories()); // безопасный IN со списком [20]
        }
        if (req != null && req.sizes() != null && !req.sizes().isEmpty()) {
            where.append("""
                and exists (
                    select 1 from product_variants pv
                    where pv.product_id = p.id
                      and pv.size in (:sizes)
                )
                """);
            params.addValue("sizes", req.sizes()); // безопасный IN со списком [20]
        }
        if (req != null && req.sizesOfBraWithCups() != null && !req.sizesOfBraWithCups().isEmpty()) {
            where.append("""
                and exists (
                    select 1 from product_variants pv2
                    where pv2.product_id = p.id
                      and pv2.size in (:sizesOfBra)
                )
                """);
            params.addValue("sizesOfBra", req.sizesOfBraWithCups()); // безопасный IN со списком [20]
        }
        if (req != null && req.colors() != null && !req.colors().isEmpty()) {
            where.append("""
                and exists (
                    select 1 from product_variants pv3
                    where pv3.product_id = p.id
                      and pv3.color_name in (:colors)
                )
                """);
            params.addValue("colors", req.colors()); // безопасный IN со списком [20]
        }

        // Белый список сортируемых полей -> SQL-колонки
        Map<String, String> sortWhitelist = Map.of(
                "productId", "p.id",
                "name", "p.name",
                "price", "pr.base_amount",
                "createdAt", "p.created_at"
        );

        String orderBy = buildOrderBy(pageable, sortWhitelist); // защищённая сортировка [12]
        String paging = " limit :limit offset :offset ";
        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", (long) pageable.getPageNumber() * pageable.getPageSize());

        String base = select.toString() + where;
        String dataSql = base + orderBy + paging;
        String countSql = "select count(*) from (" + base + ") t";

        List<ProductCatalogItemResponseDto> content =
                jdbc.query(dataSql, params, new ProductCatalogRowMapper()); // безопасный RowMapper [23]
        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalElements = total != null ? total : 0L; // защита от null [8]

        return new PageImpl<>(content, pageable, totalElements); // стандартный Page [8]
    }

    private String buildOrderBy(Pageable pageable, Map<String, String> whitelist) {
        if (pageable == null || pageable.getSort() == null || pageable.getSort().isUnsorted()) {
            return " order by p.created_at desc, p.id desc "; // дефолт, не из ввода [3]
        }
        List<String> parts = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            String col = whitelist.get(o.getProperty());
            if (col != null) {
                parts.add(col + " " + (o.isAscending() ? "asc" : "desc"));
            }
        }
        if (parts.isEmpty()) {
            return " order by p.created_at desc, p.id desc "; // fallback [3]
        }
        return " order by " + parts.stream().collect(Collectors.joining(", "));
    }
}