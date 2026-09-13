@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service"
})
package gr.grodov.grsso.authentication;

import org.springframework.modulith.ApplicationModule;