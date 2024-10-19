package br.com.api.mapper;

import br.com.api.model.livro.Livro;
import br.com.api.model.livro.LivroDto;
import org.modelmapper.ModelMapper;

public class LivroMapper {

    private static ModelMapper modelMapper = new ModelMapper();

    public static Livro mapToEntity(LivroDto livroDto) {
        return modelMapper.map(livroDto, Livro.class);
    }

    public static LivroDto mapToDto(Livro livro) {
        return modelMapper.map(livro, LivroDto.class);
    }
}
