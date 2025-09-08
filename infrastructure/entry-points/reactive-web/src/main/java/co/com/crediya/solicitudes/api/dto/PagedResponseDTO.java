package co.com.crediya.solicitudes.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PagedResponseDTO<T> {
    List<T> items;
    long total;
    int page;
    int size;
}
