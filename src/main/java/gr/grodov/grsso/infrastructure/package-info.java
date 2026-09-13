@ApplicationModule(allowedDependencies = {
    "common",
    "user :: service"
})
package gr.grodov.grsso.infrastructure;

import org.springframework.modulith.ApplicationModule;