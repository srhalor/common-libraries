# Timestamp Formatting in Error Response DTO

## Change Overview
Updated the `ErrorResponseDto` to format the `Instant` timestamp in a consistent, human-readable ISO-8601 format.

## Files Modified

### ErrorResponseDto.java
**Location:** `common-utilities/src/main/java/com/shdev/common/dto/ErrorResponseDto.java`

**Changes:**
- Added `@JsonFormat` import: `com.fasterxml.jackson.annotation.JsonFormat`
- Added `@JsonFormat` annotation to the `timestamp` field:
  ```java
  @JsonProperty("timestamp")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
  Instant timestamp;
  ```

## Timestamp Format Details

| Format | Example |
|--------|---------|
| Pattern | `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` |
| Example Output | `2025-12-18T01:46:16.123Z` |
| Timezone | UTC |
| Components | Date, Time with milliseconds, UTC indicator |

## Example Error Response

### Before (ISO-8601 without milliseconds)
```json
{
  "timestamp": "2025-12-18T01:46:16Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Missing JWT token",
  "path": "/api/protected-endpoint"
}
```

### After (ISO-8601 with milliseconds)
```json
{
  "timestamp": "2025-12-18T01:46:16.123Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Missing JWT token",
  "path": "/api/protected-endpoint"
}
```

## Benefits

1. **Consistency**: All error responses now have consistently formatted timestamps
2. **Readability**: Human-readable ISO-8601 format with millisecond precision
3. **Precision**: Includes milliseconds for better debugging and log correlation
4. **Standards**: Follows RFC 3339 standard for date/time serialization
5. **Timezone**: Explicitly UTC timezone for clarity and consistency

## Build Status
✅ Build successful - No compilation errors
✅ All changes validated

## Impact
- Error responses now display timestamps in a standard, predictable format
- Timestamps include millisecond precision for better timing accuracy
- No impact on existing error handling logic - purely formatting change
- All services using `ErrorResponseDto` automatically benefit from this formatting

