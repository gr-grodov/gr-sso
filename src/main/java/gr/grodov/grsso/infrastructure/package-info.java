@ApplicationModule(allowedDependencies = {
    "common",
    "user :: domain"
})
package gr.grodov.grsso.infrastructure;

import org.springframework.modulith.ApplicationModule;