package org.example.admin.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.example.admin.dto.UiBlockType;

import java.util.List;

public record EligibilityBatchCreateRequest(
        @NotEmpty List<@Valid Item> items
) {
    public record Item(
            @NotBlank @Size(max = 120) String title,
            @Size(max = 300) String description,
            @NotBlank @Size(max = 300) String question,

            @NotNull UiBlockType type,
            List<@NotBlank String> options
    ) {

        @AssertTrue(message = "options는 select_single/select_multi인 경우 최소 2개 이상이어야 하며, 그 외 type에서는 null이어야 합니다.")
        @JsonIgnore
        public boolean isOptionsValid() {
            if (type == null) return true;

            boolean needsOptions = (type == UiBlockType.SELECT_SINGLE || type == UiBlockType.SELECT_MULTI);

            if (needsOptions) {
                return options != null && options.size() >= 2;
            }
            return options == null;
        }
    }
}