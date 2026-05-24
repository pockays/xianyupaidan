package com.xianyupaidan.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String email;
    private List<CategoryData> categories;

    @Data
    public static class CategoryData {
        private String categoryName;
        private List<ItemData> items;

        @Data
        public static class ItemData {
            private String linkUrl;
            private String note;
        }
    }
}
