package org.tkit.onecx.themes.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.bff.clients.model.ImageInfo;
import gen.org.tkit.onecx.theme.bff.clients.model.MimeType;
import gen.org.tkit.onecx.theme.bff.clients.model.RefType;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.MimeTypeDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.RefTypeDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ImageMapper {

    ImageInfoDTO map(ImageInfo image);

    ImageInfo map(ImageInfoDTO image);

    RefType map(RefTypeDTO refType);

    @ValueMapping(target = "X_MINUS_ICON", source = "X_ICON")
    MimeType mapMimeType(MimeTypeDTO mimeType);
}
