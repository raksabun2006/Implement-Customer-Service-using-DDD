package co.istad.config;

import co.istad.document.CustomerSegmentDocument;
import co.istad.valueObject.CustomerSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableReactiveMongoAuditing
public class MongoCofig {

    /**
     * Enables reactive auditing for @CreatedBy and @LastModifiedBy.
     * Returns the current auditor identity (defaults to "SYSTEM").
     */
    @Bean
    public ReactiveAuditorAware<String> reactiveAuditorAware() {
        return () -> Mono.just("SYSTEM");
    }

    /**
     * Configures reactive transaction management for MongoDB replica sets.
     */
    @Bean
    public ReactiveMongoTransactionManager reactiveMongoTransactionManager(ReactiveMongoDatabaseFactory databaseFactory) {
        return new ReactiveMongoTransactionManager(databaseFactory);
    }

    /**
     * Automatically verifies and initializes default customer segments
     * (SILVER, GOLD, PREMIUM) in the 'customer_segments' collection on startup.
     */
    @Bean
    public CommandLineRunner initCustomerSegments(ReactiveMongoTemplate reactiveMongoTemplate) {
        return args -> {
            log.info("Checking and initializing default customer segments in MongoDB...");
            Flux.just(CustomerSegment.values())
                    .flatMap(segment -> {
                        CustomerSegmentDocument segmentDoc = CustomerSegmentDocument.builder()
                                .id(segment.name())
                                .name(segment.name())
                                .description(segment.name() + " tier customer segment")
                                .build();
                        return reactiveMongoTemplate.findById(segment.name(), CustomerSegmentDocument.class)
                                .switchIfEmpty(reactiveMongoTemplate.save(segmentDoc));
                    })
                    .doOnComplete(() -> log.info("Default customer segments verified."))
                    .onErrorResume(e -> {
                        log.warn("Could not pre-populate customer segments (MongoDB may be offline): {}", e.getMessage());
                        return Mono.empty();
                    })
                    .subscribe();
        };
    }
}
