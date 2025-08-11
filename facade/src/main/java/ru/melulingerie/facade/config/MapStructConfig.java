package ru.melulingerie.facade.config;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * - injectionStrategy = CONSTRUCTOR - инжектирование зависимостей через конструктор
 * - unmappedTargetPolicy = IGNORE - игнорирование несоответствующих полей в целевом объекте
 * - unmappedSourcePolicy = IGNORE - игнорирование несоответствующих полей в исходном объекте
 */
@MapperConfig(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructConfig {
}