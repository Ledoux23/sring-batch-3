package com.spring_batch_3.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.spring_batch_3.model.Product;

@Component 
public class ProductProcessor implements ItemProcessor<Product, Product> {

	@Override
    public Product process(Product product) {
        // Filtrer les produits avec un prix supérieur à 50
        if (product.getPrice() > 50) {
            // Appliquer une augmentation de 20% sur le prix
            product.setPrice(product.getPrice() * 1.2);
            return product;
        }
        return null;  // Le produit est filtré
    }
	
}
