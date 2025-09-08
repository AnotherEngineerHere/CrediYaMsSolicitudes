package co.com.crediya.solicitudes.model.solicitud;

import lombok.Value;

@Value
public class PageQuery {
    int page;      // 0-based
    int size;      // 1..200
    String sortBy; // campo permitido
    boolean asc;
}
