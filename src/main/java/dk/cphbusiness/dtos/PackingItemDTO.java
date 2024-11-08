package dk.cphbusiness.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackingItemDTO {

    private String name;
    private int weightInGrams;
    private int quantity;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BuyingOptionDTO> buyingOptions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BuyingOptionDTO {
        private String shopName;
        private String shopUrl;
        private double price;
    }
}