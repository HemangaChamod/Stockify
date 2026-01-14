package com.inventory.backend.controller;

import com.inventory.backend.model.Item;
import com.inventory.backend.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/items")
@CrossOrigin
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(produces = "application/json")
public ResponseEntity<?> getAllItems() {
    return ResponseEntity.ok(itemService.getAllItems());
}


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Item> addItem(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double sellingPrice,
            @RequestParam double costPrice,
            @RequestParam String unit,
            @RequestParam int stockInHand,
            @RequestPart MultipartFile image
    ) throws Exception {

        Item savedItem = itemService.saveItem(
                name,
                description,
                sellingPrice,
                costPrice,
                unit,
                stockInHand,
                image
        );

        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{id}")
        public ResponseEntity<Item> updateItem(
                @PathVariable Long id,
                @RequestBody Item item
        ) {
            Item updatedItem = itemService.updateItem(
                    id,
                    item.getName(),
                    item.getDescription(),
                    item.getSellingPrice(),
                    item.getCostPrice(),
                    item.getUnit(),
                    item.getStockInHand()
            );

        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
            itemService.deleteItem(id);
            return ResponseEntity.noContent().build(); // 204
    }
}
