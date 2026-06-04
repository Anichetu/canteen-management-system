package com.canteen.service;

import com.canteen.model.MenuItem;
import com.canteen.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllAvailableItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    public List<MenuItem> searchMenuItems(String query) {
        return menuItemRepository.searchByName(query);
    }

    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    @Transactional
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItem updated) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        item.setName(updated.getName());
        item.setPrice(updated.getPrice());
        item.setCategory(updated.getCategory());
        item.setAvailable(updated.getAvailable());
        item.setDescription(updated.getDescription());
        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Transactional
    public MenuItem toggleAvailability(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        item.setAvailable(!item.getAvailable());
        return menuItemRepository.save(item);
    }
}
