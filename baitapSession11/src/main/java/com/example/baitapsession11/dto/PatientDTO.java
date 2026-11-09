package com.example.baitapsession11.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDTO {
    @NotBlank(message = "Tên bệnh nhân không được để trống")
    private String name;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 0, message = "Tuổi không được âm")
    private Integer age;
}
