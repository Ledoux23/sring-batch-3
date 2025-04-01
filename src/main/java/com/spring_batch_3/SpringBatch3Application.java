package com.spring_batch_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatch3Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatch3Application.class, args);
	}

}
/*
 * Structure du projet
 * 
└── src/
    ├── main/
        ├── java/
            └── com/example/springbatch/
                ├── SpringBatchApplication.java
                ├── config/
                │   ├── BatchConfig.java
                │   └── CsvReaderConfig.java
                ├── model/
                │   └── Product.java
                ├── processor/
                │   └── ProductProcessor.java
                ├── repository/
                │   └── ProductRepository.java
                ├── writer/
                │   └── DatabaseWriter.java
                └── resources/
                    └── products.csv
*/