package com.oasis.binary_honam.dto.Spot;

import com.oasis.binary_honam.entity.ClearStory;
import com.oasis.binary_honam.entity.Spot;
import com.oasis.binary_honam.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SpotCreateRequest {
    private String spotName;
    private String spotAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private String spotDes;
}
