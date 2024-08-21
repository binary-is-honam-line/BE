package com.oasis.binary_honam.dto.OpenAi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryDto {
    private List<StageDto> stageDtoList;
    private MainDto mainDto;
}
