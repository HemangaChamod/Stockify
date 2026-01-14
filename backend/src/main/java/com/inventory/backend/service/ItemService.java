package com.inventory.backend.service;

import com.inventory.backend.model.Item;
import com.inventory.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;         
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    // Folder where images will be stored
    private static final String IMAGE_DIR =
            System.getProperty("user.home") + "/Stockify/images/";

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(
            String name,
            String description,
            double sellingPrice,
            double costPrice,
            String unit,
            int stockInHand,
            MultipartFile image
    ) throws IOException {

        // Create directory if missing
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "default.png"; // fallback image

        // Save image only if uploaded
        if (image != null && !image.isEmpty()) {

            String originalName = image.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            fileName = UUID.randomUUID().toString() + extension;

            Path imagePath = Paths.get(IMAGE_DIR, fileName);
            Files.write(imagePath, image.getBytes());
        }   

        // Calculate stock value
        double totalStockValue = sellingPrice * stockInHand;

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setSellingPrice(sellingPrice);
        item.setCostPrice(costPrice);
        item.setUnit(unit);
        item.setStockInHand(stockInHand);
        item.setTotalStockValue(totalStockValue);

        // Store only filename
        item.setImagePath(fileName);

        return itemRepository.save(item);
    }

    public Item updateItem(
        Long id,
        String name,
        String description,
        double sellingPrice,
        double costPrice,
        String unit,
        int stockInHand
) {
    Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));

    item.setName(name);
    item.setDescription(description);
    item.setSellingPrice(sellingPrice);
    item.setCostPrice(costPrice);
    item.setUnit(unit);
    item.setStockInHand(stockInHand);

    double totalStockValue = sellingPrice * stockInHand;
    item.setTotalStockValue(totalStockValue);

    return itemRepository.save(item);
}

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        itemRepository.deleteById(id);
    }

}
