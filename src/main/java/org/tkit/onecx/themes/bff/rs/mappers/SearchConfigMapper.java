package org.tkit.onecx.themes.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.searchconfig.v1.client.model.*;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface SearchConfigMapper {
    @Mapping(source = "isReadonly", target = "readOnly")
    @Mapping(source = "isAdvanced", target = "advanced")
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "appId", ignore = true)
    CreateSearchConfigRequest map(CreateSearchConfigRequestDTO configRequestDTO);

    @Mapping(target = "removeConfigsItem", ignore = true)
    @Mapping(target = "configs", source = "stream")
    GetSearchConfigInfosResponseDTO map(SearchConfigSearchPageResult searchConfigSearchPageResult);

    List<SearchConfigInfoDTO> mapInfo(List<SearchConfigSearchResult> searchResultList);

    @Mapping(target = "id", source = "configId")
    SearchConfigInfoDTO mapInfo(SearchConfigSearchResult searchResult);

    @Mapping(target = "removeConfigsItem", ignore = true)
    @Mapping(target = "configs", source = "stream")
    CreateSearchConfigResponseDTO mapCreate(SearchConfigSearchPageResult searchConfigSearchPageResult);

    @Mapping(target = "removeConfigsItem", ignore = true)
    @Mapping(target = "configs", source = "stream")
    UpdateSearchConfigResponseDTO mapUpdate(SearchConfigSearchPageResult searchConfigSearchPageResult);

    List<SearchConfigDTO> map(List<SearchConfigSearchResult> searchResultList);

    @Mapping(target = "values", ignore = true)
    @Mapping(target = "removeValuesItem", ignore = true)
    @Mapping(target = "removeColumnsItem", ignore = true)
    @Mapping(target = "page", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "isReadonly", ignore = true)
    @Mapping(target = "isAdvanced", ignore = true)
    @Mapping(target = "fieldListVersion", ignore = true)
    @Mapping(target = "columns", ignore = true)
    @Mapping(target = "id", source = "configId")
    SearchConfigDTO map(SearchConfigSearchResult searchResult);

    @Mapping(target = "removeValuesItem", ignore = true)
    @Mapping(target = "removeColumnsItem", ignore = true)
    @Mapping(target = "id", source = "configId")
    @Mapping(target = "isAdvanced", source = "advanced")
    @Mapping(target = "isReadonly", source = "readOnly")
    SearchConfigDTO map(SearchConfig searchConfig);

    @Mapping(target = "values", source = "searchConfig.values")
    @Mapping(target = "readOnly", source = "searchConfig.isReadonly")
    @Mapping(target = "name", source = "searchConfig.name")
    @Mapping(target = "modificationCount", source = "searchConfig.modificationCount")
    @Mapping(target = "fieldListVersion", source = "searchConfig.fieldListVersion")
    @Mapping(target = "columns", source = "searchConfig.columns")
    @Mapping(target = "advanced", source = "searchConfig.isAdvanced")
    UpdateSearchConfigRequest update(UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO);
}
