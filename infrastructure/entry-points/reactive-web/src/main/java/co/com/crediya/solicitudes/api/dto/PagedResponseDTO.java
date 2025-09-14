package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * DTO para respuestas paginadas genéricas.
 */
@Value
@Builder
@Schema(description = "Respuesta paginada genérica")
public class PagedResponseDTO<T> {

    @Schema(description = "Lista de elementos de la página actual")
    List<T> items;

    @Schema(description = "Total de elementos disponibles", example = "150")
    long total;

    @Schema(description = "Número de página actual (0-based)", example = "0")
    int page;

    @Schema(description = "Tamaño de la página", example = "10")
    int size;
}
