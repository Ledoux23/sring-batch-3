package com.spring_batch_3.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring_batch_3.model.Product;
import com.spring_batch_3.processor.ProductProcessor;

@Configuration 
@EnableBatchProcessing 
public class BatchConfig {
	
	@Bean
    public Job importProductJob(JobRepository jobRepository, Step processStep) {
        return new JobBuilder("importProductJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processStep)
                .build();
    }

    @Bean
    public Step processStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            ItemReader<Product> reader, ItemProcessor<Product, Product> processor, ItemWriter<Product> writer) {
        return new StepBuilder("processStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    
  
}
