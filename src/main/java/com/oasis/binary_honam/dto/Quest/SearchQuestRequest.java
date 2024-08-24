package com.oasis.binary_honam.dto.Quest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchQuestRequest {
    private String keyword;
    private String location;
}
