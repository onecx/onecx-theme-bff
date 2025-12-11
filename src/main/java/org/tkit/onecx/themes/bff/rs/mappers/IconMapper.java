package org.tkit.onecx.themes.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.bff.clients.model.IconCriteria;
import gen.org.tkit.onecx.theme.bff.clients.model.IconListResponse;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.IconCriteriaDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.IconListResponseDTO;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface IconMapper {
    IconCriteria mapCriteria(IconCriteriaDTO iconCriteriaDTO);

    @Mapping(target = "removeIconsItem", ignore = true)
    IconListResponseDTO map(IconListResponse iconListResponse);
}
