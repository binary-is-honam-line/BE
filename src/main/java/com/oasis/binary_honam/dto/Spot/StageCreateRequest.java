package com.oasis.binary_honam.dto.Spot;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class StageCreateRequest {
    private String stageName;
    private String stageAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private String stageDes;
}
