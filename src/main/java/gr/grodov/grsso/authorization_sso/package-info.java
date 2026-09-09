@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service", "user :: domain",
    "oauth_client :: repo", "oauth_client :: service",
    "authentication :: service",
    "session_sso :: service"
})
package gr.grodov.grsso.authorization_sso;

import org.springframework.modulith.ApplicationModule;