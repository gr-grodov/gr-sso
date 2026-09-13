@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service",
    "oauth_client :: repo", "oauth_client :: service",
    "authentication :: service",
    "oauth_session :: service"
})
package gr.grodov.grsso.authorization_sso;

import org.springframework.modulith.ApplicationModule;