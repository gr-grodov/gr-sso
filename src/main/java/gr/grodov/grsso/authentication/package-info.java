@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service", "user :: domain"
})
package gr.grodov.grsso.authentication;

import org.springframework.modulith.ApplicationModule;