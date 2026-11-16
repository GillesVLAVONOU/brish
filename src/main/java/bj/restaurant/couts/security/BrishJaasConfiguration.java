package bj.restaurant.couts.security;

import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public final class BrishJaasConfiguration extends Configuration {
    private static final AppConfigurationEntry[] LOGIN_MODULES = {
        new AppConfigurationEntry(
            BrishLoginModule.class.getName(),
            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
            Map.of())
    };

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if (!"Brish".equals(name)) {
            return null;
        }
        return LOGIN_MODULES.clone();
    }
}
