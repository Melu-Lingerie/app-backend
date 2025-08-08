package ru.mellingerie.api.common;

import java.util.Map;

public record ApiError(ErrorCode code, String message, Map<String, Object> details) {}


