package io.github.onecx.themes.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.theme.bff.rs.internal.model.CreateThemeRequestDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.SearchThemeRequestDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.UpdateThemeRequestDTO;

@ApplicationScoped
public class ThemeLog implements LogParam {
    @Override
    public List<Item> getClasses() {
        return List.of(
                this.item(10, CreateThemeRequestDTO.class,
                        x -> "CreateThemeRequestDTO[ name: " +
                                ((CreateThemeRequestDTO) x).getResource().getName()
                                + " ]"),
                this.item(10, UpdateThemeRequestDTO.class,
                        x -> "UpdateThemeRequestDTO[ name: " + ((UpdateThemeRequestDTO) x).getResource().getName()
                                + " ]"),
                this.item(10, SearchThemeRequestDTO.class,
                        x -> "SearchThemeRequestDTO[ name: " + ((SearchThemeRequestDTO) x).getName()
                                + " ]"));
    }
}
