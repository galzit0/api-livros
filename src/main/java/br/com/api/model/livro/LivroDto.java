package br.com.api.model.livro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para requisição de criação ou atualização de uma condição.")
public class LivroDto {

    private Long id;

    @Schema(description = "Título.", example = "Código Limpo, Habilidades Práticas do Agile Software")
    private String titulo;

    @Schema(description = "Autor.", example = "João Pequeno")
    private String autor;

    @Schema(description = "ISBN.", example = "ISBN-99: 9999999999")
    private String isbn;

    @Schema(description = "Status disponivel.", example = "true")
    private Boolean stDisponivel;
}
