package com.xianyupaidan.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponse {
    private Long id;
    private Long userId;
    private String nickname;
    private String email;
    private String status;
    private BigDecimal totalPrice;
    private Integer submitted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryDetail> categories;

    @Data
    public static class CategoryDetail {
        private Long id;
        private String categoryName;
        private Integer sortOrder;
        private List<ItemDetail> items;

        @Data
        public static class ItemDetail {
            private Long id;
            private String linkUrl;
            private String note;
            private BigDecimal price;
            private String status;
            private Integer sortOrder;
        }
    }
}
