package insurance.web.rest;

import insurance.service.QuoteService;
import insurance.service.dto.*;
import insurance.web.rest.consts.Url;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    @Operation(summary = "create new quote")
    @PostMapping(Url.POST_QUOTE_CREATE)
    public ResponseEntity<BaseResponseDto<Void>> createQuote(@RequestBody @Valid CreateQuoteRequestDto requestDto) {
        quoteService.createQuote(requestDto);
        return ResponseEntity.ok(BaseResponseDto.ok());
    }

    @GetMapping(Url.GET_QUOTE_RETRIEVE)
    @Operation(summary = "get existing quote by id")
    public ResponseEntity<BaseResponseDto<QuoteResponseDto>> getQuote(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponseDto.of(quoteService.getQuote(id)));
    }

    @PutMapping(Url.PUT_QUOTE_UPDATE)
    @Operation(summary = "update existing quote")
    public ResponseEntity<BaseResponseDto<Void>> updateQuote(@Valid @RequestBody UpdateQuoteRequestDto requestDto) {
         quoteService.updateQuote(requestDto);
        return ResponseEntity.ok(BaseResponseDto.ok());
    }

    @DeleteMapping(Url.DELETE_QUOTE_REMOVE)
    @Operation(summary = "delete existing quote")
    public ResponseEntity<BaseResponseDto<Void>> deleteQuote(@PathVariable Long id) {
        quoteService.deleteQuote(id);
        return ResponseEntity.ok(BaseResponseDto.ok());
    }

    @PostMapping(Url.POST_QUOTE_LIST)
    @Operation(summary = "get quote list")
    public ResponseEntity<BaseResponseDto<List<QuoteResponseDto>>> list(@RequestBody GetQuoteListRequestDto requestDto) {
        return ResponseEntity.ok(BaseResponseDto.of(quoteService.getQuoteList(requestDto)));
    }
}

