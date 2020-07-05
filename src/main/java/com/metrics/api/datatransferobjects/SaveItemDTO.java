package com.metrics.api.datatransferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveItemDTO {

    @NotBlank
    private String name;

    @NotBlank
    private Double value;
}
