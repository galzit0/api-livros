package br.com.api.controller;

import br.com.api.model.livro.Livro;
import br.com.api.model.livro.LivroDto;
import br.com.api.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LivroService livroService;

    @Autowired
    private ObjectMapper objectMapper;

    private Livro livro;
    private LivroDto livroDto;

    @BeforeEach
    void mockEntidadeDto() {
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro 1234");
        livro.setAutor("Gabriel Pequeno");
        livro.setIsbn("111222333444");
        livro.setDisponivel(true);

        livroDto = new LivroDto();
        livroDto.setId(1L);
        livroDto.setTitulo("Livro 1234");
        livroDto.setAutor("Gabriel Pequeno");
        livroDto.setIsbn("111222333444");
        livroDto.setDisponivel(true);
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testGetAllLivros() throws Exception {
        Mockito.when(livroService.getAllLivros()).thenReturn(Arrays.asList(livro));

        mockMvc.perform(get("/livro/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Livro 1234"))
                .andExpect(jsonPath("$[0].autor").value("Gabriel Pequeno"));
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testGetLivroById() throws Exception {
        Mockito.when(livroService.get(1L)).thenReturn(Optional.of(livro));

        mockMvc.perform(get("/livro/get/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Livro 1234"))
                .andExpect(jsonPath("autor").value("Gabriel Pequeno"));
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testGetLivroByIdNotFound() throws Exception {
        Mockito.when(livroService.get(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/livro/get/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testSaveLivro() throws Exception {
        Mockito.when(livroService.saveCreate(any(Livro.class))).thenReturn(ResponseEntity.ok(livro));

        mockMvc.perform(MockMvcRequestBuilders.post("/livro/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Livro 1234"))
                .andExpect(jsonPath("$.autor").value("Gabriel Pequeno"));
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testUpdateLivro() throws Exception {
        Mockito.when(livroService.saveUpdate(any(Livro.class))).thenReturn(ResponseEntity.ok(livro));

        mockMvc.perform(MockMvcRequestBuilders.put("/livro/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Livro 1234"))
                .andExpect(jsonPath("$.autor").value("Gabriel Pequeno"));
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testDeletarLivro() throws Exception {
        Mockito.when(livroService.get(1L)).thenReturn(Optional.of(livro));
        Mockito.doNothing().when(livroService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/livro/delete/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "gabriel", authorities = {"Administrador"})
    void testDeletarLivroNotFound() throws Exception {
        Mockito.when(livroService.get(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/livro/delete/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
