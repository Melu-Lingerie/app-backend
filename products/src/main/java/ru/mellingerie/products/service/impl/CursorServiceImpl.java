package ru.mellingerie.products.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.service.CursorService;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class CursorServiceImpl implements CursorService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public CursorData decode(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }
        
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor));
            return objectMapper.readValue(decoded, CursorData.class);
        } catch (Exception e) {
            log.warn("Failed to decode cursor: {}", cursor, e);
            return null;
        }
    }
    
    @Override
    public String encode(CursorData cursorData) {
        if (cursorData == null) {
            return null;
        }
        
        try {
            String json = objectMapper.writeValueAsString(cursorData);
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (JsonProcessingException e) {
            log.error("Failed to encode cursor data: {}", cursorData, e);
            throw new RuntimeException("Failed to encode cursor", e);
        }
    }
    
    @Override
    public CursorData initial() {
        return new CursorData(0, 0L, 0L);
    }
} 