package insurance.web.rest;

import insurance.service.AggregationService;
import insurance.service.dto.AggregationRequestDto;
import insurance.service.dto.AggregationResultDto;
import insurance.service.dto.BaseResponseDto;
import insurance.web.rest.consts.Url;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AggregationController {
    private final AggregationService aggregationService;

    @PostMapping(Url.POST_QUOTE_AGGREGATION)
    public ResponseEntity<BaseResponseDto<AggregationResultDto>> aggregate(@RequestBody @Valid AggregationRequestDto requestDto) {
        return ResponseEntity.ok(BaseResponseDto.of(aggregationService.getAggregatedData(requestDto)));
    }
}

