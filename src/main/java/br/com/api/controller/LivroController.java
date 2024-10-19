package br.com.api.controller;

import br.com.api.mapper.LivroMapper;
import br.com.api.model.livro.Livro;
import br.com.api.model.livro.LivroDto;
import br.com.api.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/livro", path = "/livro")
@RequiredArgsConstructor
@Tag(description = "Dados do livro", name = "Dados de livro")
public class LivroController {

    private final LivroService livroService;

    @GetMapping(path = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Administrador')")
    @Operation(
            summary = "Retorna a lista de municipios.",
            description = "Retorna a lista de municipios"
    )
    public List<LivroDto> getAllLivros() {
        return livroService.getAllLivros().stream()
                .map(LivroMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/getAllPage", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Administrador')")
    @Operation(
            summary = "Lista todos os Municipios.",
            description = "Retorna a lista de todos os Municipios"
    )
    public Page<LivroDto> getAllPage(
            Livro livro,
            @PageableDefault
            @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Livro> page = livroService.getAllPage(livro, pageable);
        List<LivroDto> livroDtoList = page.getContent().stream()
                .map(LivroMapper::mapToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(livroDtoList, pageable, page.getTotalElements());
    }

    @GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Administrador')")
    @Operation(
            summary = "Retorna um Livro.",
            description = "Retorna um Livro com base no seu ID."
    )
    public ResponseEntity<LivroDto> get(@PathVariable("id") Long id) {
        var livro = livroService.get(id);
        return livro.map(value -> ResponseEntity.ok(LivroMapper.mapToDto(value))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Administrador')")
    @Operation(
            summary = "Salvar Livro.",
            description = "Salva um novo Livro no sistema."
    )
    public ResponseEntity<LivroDto> save(@RequestBody LivroDto livroDto) {
        Livro livroAtualizado = livroService.saveCreate(LivroMapper.mapToEntity(livroDto)).getBody();
        return ResponseEntity.ok(LivroMapper.mapToDto(livroAtualizado));
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Administrador')")
    @Operation(
            summary = "Alterar Livro.",
            description = "Altera um Livro já existente no sistema."
    )
    public ResponseEntity<LivroDto> update(@PathVariable("id") Long id, @RequestBody LivroDto livroDto) {
        Livro livroAtualizado = livroService.saveUpdate(LivroMapper.mapToEntity(livroDto)).getBody();
        return ResponseEntity.ok(LivroMapper.mapToDto(livroAtualizado));
    }

    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Deletar Livro.",
            description = "Deleta um Livro já existente no sistema."
    )
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        var livro = livroService.get(id);
        return livro.map(value -> {
                    livroService.delete(id);
                    return new ResponseEntity<>("Livro "+ livro.get().getTitulo() +" excluído", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


}
