package com.spring_batch_3.writer;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.spring_batch_3.model.Product;
import com.spring_batch_3.repository.ProductRepository;

@Configuration 
public class DatabaseWriterConfig {
	
	@Bean
    public JdbcBatchItemWriter<Product> writer(DataSource dataSource) {
        JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO product (name, price) VALUES (:name, :price)");
        writer.setDataSource(dataSource);
        writer.afterPropertiesSet(); // Nécessaire pour initialiser correctement le writer
        return writer;
    }
}
