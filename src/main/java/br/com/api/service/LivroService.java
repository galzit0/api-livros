package br.com.api.service;

import br.com.api.model.livro.Livro;
import br.com.api.model.usuario.UsuarioDto;
import br.com.api.repository.LivroRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import static br.com.api.config.RabbitMQConfig.EXCHANGE_NAME;
import static br.com.api.config.RabbitMQConfig.ROUTING_KEY;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EmailService emailService;

    @Operation(summary = "Busca todos os livros",
            description = "Retorna uma lista com todos os livros cadastrados.")
    public List<Livro> getAllLivros() {
        return livroRepository.findAll();
    }

    @Operation(summary = "Cria um novo livro",
            description = "Cadastra um novo livro no sistema e notifica via RabbitMQ.")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Livro> saveCreate(Livro livro) {
        livro.setId(null);
        livro.setDisponivel(true);
        Livro createdLivro = livroRepository.save(livro);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "Livro criado: " + createdLivro.getTitulo());
        return new ResponseEntity<>(createdLivro, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza um livro existente",
            description = "Atualiza as informações de um livro já cadastrado e notifica via RabbitMQ.")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Livro> saveUpdate(Livro livro) {
        return livroRepository.findById(livro.getId())
                .map(existingLivro -> {
                    Livro updatedLivro = livroRepository.save(livro);
                    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY,"Livro atualizado: " + updatedLivro.getTitulo());
                    return new ResponseEntity<>(updatedLivro, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @Operation(summary = "Remove um livro",
            description = "Remove um livro do sistema pelo ID e notifica via RabbitMQ.")
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        livroRepository.findById(id).ifPresent(livro -> {
            livroRepository.deleteById(id);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "Livro removido: " + livro.getTitulo());
        });
    }

    @Operation(summary = "Busca um livro pelo ID",
            description = "Busca um livro específico pelo seu ID.")
    public Optional<Livro> get(@PathVariable("id") Long id) {
        return livroRepository.findById(id);
    }

    @Operation(summary = "Busca livros com paginação",
            description = "Retorna uma página de livros que correspondem ao filtro fornecido.")
    public Page<Livro> getAllPage(Livro filter, Pageable pageable) {
        return livroRepository.findAll(createExample(filter), pageable);
    }

    @Operation(summary = "Cria um filtro de busca",
            description = "Cria um exemplo de correspondência insensível a maiúsculas e minúsculas.")
    private Example<Livro> createExample(Livro filter) {
        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
        return Example.of(filter, caseInsensitiveExampleMatcher);
    }

    @Transactional
    @Operation(summary = "Aluga um livro",
              description = "Marca um livro como indisponivel e setta o UUID do usuário ao livro.")
    public ResponseEntity<String> alugarLivro(Long livroId) {
        Optional<Livro> livroOptional = livroRepository.findById(livroId);

        var usuarioDto = getUsuarioLogado();

        if (livroOptional.isEmpty()) {
            return new ResponseEntity<>("Livro não encontrado", HttpStatus.NO_CONTENT);
        }

        Livro livro = livroOptional.get();
        if (!livro.getDisponivel()) {
            return new ResponseEntity<>("Livro indisponível", HttpStatus.CONFLICT);
        }
        livro.setDisponivel(false);
        livro.setUuidUsuarioKeycloak(usuarioDto.getUuidUsuarioKeyCloak());
        livroRepository.save(livro);

        String menssagem = "O livro '" + livro.getTitulo() + "' foi alugado com sucesso!";
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, menssagem + livro.getTitulo());

        //TODO Comentando para não ser chamado, pois o servico precisa ser configurado com credenciais reais para que funcione.
        //TODO As configurações são manipuladas no application.properties.
        //emailService.enviarEmail(usuarioDto.getEmail(), "Confirmação de Aluguel de livro", menssagem);

        return new ResponseEntity<>("Livro alugado com sucesso", HttpStatus.OK);
    }

    @Transactional
    @Operation(summary = "Devolve um livro",
            description = "Marca um livro como disponivel e remove o UUID do usuário do livro.")
    public ResponseEntity<String> devolverLivro(Long livroId) {
        Optional<Livro> livroOptional = livroRepository.findById(livroId);

        var usuarioDto = getUsuarioLogado();

        if (livroOptional.isEmpty()) {
            return new ResponseEntity<>("Livro não encontrado", HttpStatus.NO_CONTENT);
        }

        Livro livro = livroOptional.get();
        if (livro.getDisponivel()) {
            return new ResponseEntity<>("Livro não está alugado", HttpStatus.CONFLICT);
        }

        livro.setDisponivel(true);
        livro.setUuidUsuarioKeycloak(null);
        livroRepository.save(livro);

        String menssagem = "O livro '" + livro.getTitulo() + "' foi devolvido com sucesso!";
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, menssagem + livro.getTitulo());

        //TODO Comentando para não ser chamado, pois o servico precisa ser configurado com credenciais reais para que funcione.
        //TODO As configurações são manipuladas no application.properties.
        //emailService.enviarEmail(usuarioDto.getEmail(), "Confirmação de devolução do livro", menssagem);

        return new ResponseEntity<>("Livro devolvido!", HttpStatus.OK);
    }

    private UsuarioDto getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        return new UsuarioDto(jwt.getClaim("sub"), jwt.getClaim("email"), jwt.getClaim("given_name"), jwt.getClaim("family_name"));
    }
}
