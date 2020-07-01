package com.metrics.api.datatransferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveItemDTO {
    private String name;


    private Double value;
}
