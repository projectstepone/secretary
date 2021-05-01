package io.appform.secretary.model.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecretaryConfiguration {

    @NotBlank
    private String kafkaTopic;

    @NotBlank
    private String serviceBaseUrl;

    @NotBlank
    private String fileUploadPath;
}
