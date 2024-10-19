package br.com.api.model.livro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para requisição de criação ou atualização de um Livro.")
public class LivroDto {

    private Long id;

    @Schema(description = "Título do livro.", example = "Código Limpo, Habilidades Práticas do Agile Software")
    private String titulo;

    @Schema(description = "Nome do autor do livro.", example = "João Pequeno")
    private String autor;

    @Schema(description = "Código ISBN do livro.", example = "ISBN-99: 9999999999")
    private String isbn;

    @Schema(description = "Indica se o livro está disponível para empréstimo.", example = "true")
    private Boolean disponivel;

    @Schema(description = "UUID do usuário no Keycloak que registrou o livro.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuidUsuarioKeycloak;
}
