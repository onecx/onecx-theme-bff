package io.github.onecx.themes.bff.rs.mappers;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.bff.clients.model.ThemePageResult;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public class ResponseMapper {
    @Inject
    ThemeMapper mapper;

    public GetThemeResponseDTO getThemeResponseDTOMapper(ThemeDTO dto) {
        GetThemeResponseDTO response = new GetThemeResponseDTO();
        response.setResource(dto);
        return response;
    }

    public CreateThemeResponseDTO createThemeResponseDTOMapper(ThemeDTO dto) {
        CreateThemeResponseDTO response = new CreateThemeResponseDTO();
        response.setResource(dto);
        return response;
    }

    public GetThemesResponseDTO getThemesResponseMapper(ThemePageResult pageResult) {
        GetThemesResponseDTO responseDTO = new GetThemesResponseDTO();
        responseDTO.setStream(pageResult.getStream().stream().map(theme -> mapper.map(theme)).toList());
        responseDTO.setNumber(pageResult.getNumber());
        responseDTO.setSize(pageResult.getSize());
        responseDTO.setTotalElements(pageResult.getTotalElements());
        responseDTO.setTotalPages(pageResult.getTotalPages());
        return responseDTO;
    }

    public SearchThemeResponseDTO searchThemeResponseMapper(ThemePageResult pageResult) {
        SearchThemeResponseDTO responseDTO = new SearchThemeResponseDTO();
        responseDTO.setStream(pageResult.getStream().stream().map(theme -> mapper.map(theme)).toList());
        responseDTO.setNumber(pageResult.getNumber());
        responseDTO.setSize(pageResult.getSize());
        responseDTO.setTotalElements(pageResult.getTotalElements());
        responseDTO.setTotalPages(pageResult.getTotalPages());
        return responseDTO;
    }

}
