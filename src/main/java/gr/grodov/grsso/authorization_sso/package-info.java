@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service", "user :: domain",
    "oauth_client :: repo",
    "authentication :: service"
})
package gr.grodov.grsso.authorization_sso;

import org.springframework.modulith.ApplicationModule;