package org.tkit.onecx.themes.bff.rs.mappers;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "onecx.search_config")
public interface SearchConfigOptions {
    @WithName("ui_app_id")
    String uiAppID();

    @WithName("product_name")
    String productName();

    @WithName("enabled")
    boolean searchConfigEnabled();
}
