package gr.grodov.grsso.attachment.props;

import gr.grodov.grsso.attachment.domain.entity.StorageType;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class StorageTypePropsConverter implements Converter<String, StorageType> {
    @Override
    public StorageType convert(String source) {
        return StorageType.valueOf(source);
    }
}
