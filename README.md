Un exemple complet d'un traitement Spring Batch avec :
✅ Step : Gestion des étapes
✅ Tasklet : Tâche simple
✅ ItemReader : Lecture des données
✅ ItemProcessor : Transformation des données
✅ ItemWriter : Écriture des résultats

📌 Scénario

Lire des produits depuis un fichier CSV, les filtrer en fonction du prix, puis écrire les produits valides dans une base de données H2.

📂 1️⃣ Structure du projet

src/main/java/com/example/batch
│── config/
│   ├── BatchConfig.java         # Configuration du batch
│   ├── CsvReaderConfig.java     # Configuration du lecteur CSV
│   ├── ProductTasklet.java      # Tasklet
│── model/
│   ├── Product.java             # Entité JPA
│── processor/
│   ├── ProductProcessor.java    # Transformation des données
│── repository/
│   ├── ProductRepository.java   # Accès base de données
│── writer/
│   ├── DatabaseWriter.java      # Écriture en base de données
│── SpringBatchApplication.java  # Classe principale

📝 2️⃣ Dépendances pom.xml

Ajoute ces dépendances si elles ne sont pas déjà présentes :

<dependencies>
    <!-- Spring Boot & Batch -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Base de données H2 -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lecture de fichiers CSV -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

🛠️ 3️⃣ Configuration application.properties

Ajoute ces lignes pour configurer la base H2 et le batch :

# Base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# Création automatique des tables
spring.jpa.hibernate.ddl-auto=update
spring.batch.jdbc.initialize-schema=always

🏷️ 4️⃣ Modèle Product.java

Créons une entité Product qui sera stockée en base.

package com.example.batch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;

    public Product() {}

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

🏛️ 5️⃣ Repository ProductRepository.java

Interface pour sauvegarder les produits en base.

package com.example.batch.repository;

import com.example.batch.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

📥 6️⃣ Lecteur CSV CsvReaderConfig.java

Lecture d'un fichier CSV contenant les produits.

package com.example.batch.config;

import com.example.batch.model.Product;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class CsvReaderConfig {

    @Bean
    public FlatFileItemReader<Product> reader() {
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("products.csv"));
        reader.setLinesToSkip(1); // Ignore la première ligne (en-têtes)

        LineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        ((DelimitedLineTokenizer) tokenizer).setNames("name", "price");

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }
}

🔄 7️⃣ Transformation des données ProductProcessor.java

Filtrer les produits avec un prix > 50.

package com.example.batch.processor;

import com.example.batch.model.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProductProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product product) {
        return (product.getPrice() > 50) ? product : null;
    }
}

📝 8️⃣ Écriture en base DatabaseWriter.java

Sauvegarde des produits filtrés.

package com.example.batch.writer;

import com.example.batch.model.Product;
import com.example.batch.repository.ProductRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseWriter implements ItemWriter<Product> {
    
    private final ProductRepository productRepository;

    public DatabaseWriter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void write(List<? extends Product> products) {
        productRepository.saveAll(products);
    }
}

🔁 9️⃣ Tasklet ProductTasklet.java

Un Tasklet simple pour afficher un message.

package com.example.batch.config;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        System.out.println("Début du traitement batch des produits...");
        return RepeatStatus.FINISHED;
    }
}

⚙ 🔟 Configuration Batch BatchConfig.java

Définition des étapes du batch.

package com.example.batch.config;

import com.example.batch.model.Product;
import com.example.batch.processor.ProductProcessor;
import com.example.batch.writer.DatabaseWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                     ProductTasklet tasklet, CsvReaderConfig csvReader, 
                     ProductProcessor processor, DatabaseWriter writer) {
        return new StepBuilder("step", jobRepository)
                .tasklet(tasklet, transactionManager)
                .<Product, Product>chunk(5, transactionManager)
                .reader(csvReader.reader())
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}

✅ Exécute l'application et vérifie que les produits sont bien filtrés et insérés en base ! 🚀
