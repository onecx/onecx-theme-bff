package org.tkit.onecx.themes.bff.rs.mappers;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "onecx.theme-bff")
public interface OnecxThemeBFFConfig {
    @WithName("ui-app-id")
    String uiAppID();

    @WithName("product-name")
    @WithDefault("${onecx.permissions.product-name}")
    String productName();

    SearchConfig searchconfig();

    interface SearchConfig {
        @WithName("enabled")
        boolean searchConfigEnabled();
    }
}