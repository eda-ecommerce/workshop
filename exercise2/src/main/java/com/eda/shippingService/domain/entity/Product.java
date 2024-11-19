package com.eda.shippingService.domain.entity;

import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Product extends AbstractEntity{
    //Should probably have more complex formats
    private Integer physicalStock;
    private Integer reservedStock;
    public Integer getAvailableStock() {
        return this.physicalStock - this.reservedStock;
    }

    private boolean retired;
    private String storageLocation;
    private Float weight;

    public Product(UUID id, Integer physicalStock) {
        this.setId(id);
        this.physicalStock = physicalStock;
        this.retired = false;
        this.storageLocation = RandomStringGenerator.generateRandomString(5);
        this.reservedStock = 0;
    }

    public void increaseStock(Integer amount) {
        if (amount < 0){
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.physicalStock = this.physicalStock + amount;
    }

    public void decreaseStock(Integer amount) {
        if (amount < 0){
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.physicalStock = this.physicalStock - amount;
    }

    public void reserveStock(Integer amount) throws NotEnoughStockException {
        if (!isQuantityAvailable(amount)) {
            throw new NotEnoughStockException(("Product with id: "+getId() + " has too few stock. Requested quantity: "+ amount));
        }
        this.reservedStock = this.reservedStock + amount;
    }

    public void releaseStock(Integer amount) {
        this.reservedStock = this.reservedStock - amount;
    }

    public boolean isQuantityAvailable(Integer amount) {
        return this.getAvailableStock() > amount;
    }

    public boolean isCritical() {
        return this.getAvailableStock() <= 5;
    }

    public void retire() {
        this.retired = true;
    }

    private static class RandomStringGenerator {
        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final Random RANDOM = new Random();

        public static String generateRandomString(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(index));
            }
            return sb.toString();
        }
    }
}
