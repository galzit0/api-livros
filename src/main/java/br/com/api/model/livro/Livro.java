package br.com.api.model.livro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "tb_livro")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro", nullable = false)
    @Schema(description = "Identificador único do Condicao. Gerado automaticamente pela estratégia de auto incremento.")
    private Long id;

    @Column(name = "titulo", nullable = false)
    @NotBlank(message = "O título do livro é obrigatório.")
    @Schema(description = "Título do livro.", example = "Código Limpo, Habilidades Práticas do Agile Software")
    private String titulo;

    @Column(name = "autor", nullable = false)
    @NotBlank(message = "O nome do autor é obrigatório.")
    @Schema(description = "Nome do autor do livro.", example = "João Pequeno")
    private String autor;

    @Column(name = "isbn", nullable = false)
    @NotBlank(message = "O ISBN do livro é obrigatório.")
    @Schema(description = "Código ISBN do livro.", example = "9999999999")
    private String isbn;

    @Column(name = "disponivel")
    @Schema(description = "Indica se o livro está disponível para empréstimo.", example = "true")
    private Boolean disponivel;

    @Column(name = "Uuid_usuario_keycloak")
    @Schema(description = "UUID do usuário no Keycloak que registrou o livro.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuidUsuarioKeycloak;

}
