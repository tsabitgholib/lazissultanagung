package com.lazis.lazissultanagung.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosDashboardResponse {
    private List<CategoryNominalSummary> categoryNominalSummary;
    private List<CategoryCountSummary> categoryCountSummary;
    private List<PaymentMethodSummary> paymentMethodSummary;
    private List<EventSummary> eventSummary;
    private TargetSummary targetSummary;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TargetSummary {
        private Double target;
        private Double currentTotal;
        private Double percentage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryNominalSummary {
        private String category;
        private Double totalNominal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryCountSummary {
        private String category;
        private Long totalCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentMethodSummary {
        private String method;
        private Long totalCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventSummary {
        private String eventName;
        private Double totalNominal;
    }
}
