package org.tkit.onecx.themes.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;

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
                                + " ]"),
                this.item(10, ExportThemeRequestDTO.class,
                        x -> "ExportThemeRequestDTO[ name: " + ((ExportThemeRequestDTO) x).getNames().toString()
                                + " ]"),
                this.item(10, ThemeSnapshotDTO.class,
                        x -> "ThemeSnapshotDTO[ name: " + ((ThemeSnapshotDTO) x).getThemes().toString()
                                + " ]"));
    }
}
