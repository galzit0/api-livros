package br.com.api.model.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para obter as informações de uum Usuário, as informçaões são obtidas a partir do keycloak")
public class UsuarioDto {

    @Schema(description = "UUID.", example = "bed91141-f8f5-478f-8ade-6e7fb9cb9ff3")
    private String uuidUsuarioKeyCloak;

    @Schema(description = "E-mail.", example = "teste@gmail.com")
    private String email;

    @Schema(description = "Primeiro Nome.", example = "Gabriel")
    private String primeiroNome;

    @Schema(description = "Ultimo Nome.", example = "Pequeno")
    private String ultimoNome;

}
