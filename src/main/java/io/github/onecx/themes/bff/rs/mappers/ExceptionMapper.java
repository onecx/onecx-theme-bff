package io.github.onecx.themes.bff.rs.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.bff.rs.internal.model.ProblemDetailInvalidParamDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.ProblemDetailParamDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.ProblemDetailResponseDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExceptionMapper {

    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    public abstract ProblemDetailResponseDTO exception(String errorCode, String detail);

    public List<ProblemDetailParamDTO> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParamDTO();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    public abstract List<ProblemDetailInvalidParamDTO> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ProblemDetailInvalidParamDTO createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}