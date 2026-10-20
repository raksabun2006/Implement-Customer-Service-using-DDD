package co.istad.adapter;

import co.istad.CustomerId;
import co.istad.document.CustomerDocument;
import co.istad.dto.CustomerQueryFilter;
import co.istad.dto.CustomerReportResult;
import co.istad.dto.CustomerStatisticResult;
import co.istad.entity.Customer;
import co.istad.mapper.CustomerPersistenceMapper;
import co.istad.port.out.CustomerRepository;
import co.istad.repository.CustomerMongoRepository;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import com.mongodb.DBRef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument customerDocument = customerPersistenceMapper.toDocument(customer);
        return customerMongoRepository.save(customerDocument)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Customer> findById(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Mono.empty();
        }
        return findById(customerId.value());
    }

    @Override
    public Mono<Customer> findById(UUID customerId) {
        if (customerId == null) {
            return Mono.empty();
        }
        return customerMongoRepository.findById(customerId)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerMongoRepository.findAll()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Mono.just(false);
        }
        log.info("Checking if customer exists reactively by username: {}", username);
        return customerMongoRepository.existsByUsername(username);
    }

    @Override
    public Mono<Customer> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Mono.empty();
        }
        log.info("Finding customer reactively by username: {}", username);
        return customerMongoRepository.findByUsername(username)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Mono.empty();
        }
        return deleteById(customerId.value());
    }

    @Override
    public Mono<Void> deleteById(UUID customerId) {
        if (customerId == null) {
            return Mono.empty();
        }
        log.info("Deleting customer document reactively by id: {}", customerId);
        return customerMongoRepository.deleteById(customerId);
    }

    @Override
    public Flux<Customer> findByFilter(CustomerQueryFilter filter) {
        Query query = buildFilterQuery(filter, true);
        log.debug("Executing findByFilter with query: {}", query);
        return reactiveMongoTemplate.find(query, CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Long> countByFilter(CustomerQueryFilter filter) {
        Query countQuery = buildFilterQuery(filter, false);
        return reactiveMongoTemplate.count(countQuery, CustomerDocument.class);
    }

    @Override
    public Mono<CustomerReportResult> getCustomerReport() {
        log.info("Executing database-side aggregation for customer report");

        Aggregation statusAgg = Aggregation.newAggregation(
                Aggregation.group("status").count().as("count")
        );

        Aggregation segmentAgg = Aggregation.newAggregation(
                Aggregation.group("segment").count().as("count")
        );

        Aggregation statusSegmentAgg = Aggregation.newAggregation(
                Aggregation.group("status", "segment").count().as("count")
        );

        Mono<Map<CustomerStatus, Long>> statusCountsMono = reactiveMongoTemplate.aggregate(statusAgg, "customers", Document.class)
                .collectMap(
                        doc -> parseCustomerStatus(doc.get("_id")),
                        doc -> getCount(doc.get("count"))
                ).defaultIfEmpty(new EnumMap<>(CustomerStatus.class));

        Mono<Map<CustomerSegment, Long>> segmentCountsMono = reactiveMongoTemplate.aggregate(segmentAgg, "customers", Document.class)
                .collectMap(
                        doc -> parseCustomerSegment(doc.get("_id")),
                        doc -> getCount(doc.get("count"))
                ).defaultIfEmpty(new EnumMap<>(CustomerSegment.class));

        Mono<Map<String, Long>> statusSegmentCountsMono = reactiveMongoTemplate.aggregate(statusSegmentAgg, "customers", Document.class)
                .collectMap(
                        this::parseStatusSegmentKey,
                        doc -> getCount(doc.get("count"))
                ).defaultIfEmpty(new HashMap<>());

        Mono<Long> totalCountMono = customerMongoRepository.count();

        return Mono.zip(totalCountMono, statusCountsMono, segmentCountsMono, statusSegmentCountsMono)
                .map(tuple -> {
                    long total = tuple.getT1();
                    Map<CustomerStatus, Long> statusMap = new EnumMap<>(CustomerStatus.class);
                    for (CustomerStatus cs : CustomerStatus.values()) {
                        statusMap.put(cs, tuple.getT2().getOrDefault(cs, 0L));
                    }

                    Map<CustomerSegment, Long> segmentMap = new EnumMap<>(CustomerSegment.class);
                    for (CustomerSegment seg : CustomerSegment.values()) {
                        segmentMap.put(seg, tuple.getT3().getOrDefault(seg, 0L));
                    }

                    return new CustomerReportResult(total, statusMap, segmentMap, tuple.getT4());
                });
    }

    @Override
    public Mono<CustomerStatisticResult> getCustomerStatistic() {
        log.info("Executing database-side aggregation for customer statistics");

        return getCustomerReport().map(report -> {
            long total = report.totalCustomers();
            long active = report.countByStatus().getOrDefault(CustomerStatus.ACTIVE, 0L);
            long inactive = report.countByStatus().getOrDefault(CustomerStatus.INACTIVE, 0L);
            long silver = report.countBySegment().getOrDefault(CustomerSegment.SILVER, 0L);
            long gold = report.countBySegment().getOrDefault(CustomerSegment.GOLD, 0L);
            long premium = report.countBySegment().getOrDefault(CustomerSegment.PREMIUM, 0L);

            return new CustomerStatisticResult(
                    total,
                    active,
                    inactive,
                    silver,
                    gold,
                    premium,
                    report.countByStatus(),
                    report.countBySegment(),
                    Instant.now()
            );
        });
    }

    private Query buildFilterQuery(CustomerQueryFilter filter, boolean includePaginationAndSort) {
        List<Criteria> andCriteria = new ArrayList<>();

        if (filter != null) {
            // Keyword search across username, familyName, givenName, email, phoneNumber
            if (filter.search() != null && !filter.search().trim().isEmpty()) {
                String term = Pattern.quote(filter.search().trim());
                Criteria searchCriteria = new Criteria().orOperator(
                        Criteria.where("username").regex(term, "i"),
                        Criteria.where("familyName").regex(term, "i"),
                        Criteria.where("givenName").regex(term, "i"),
                        Criteria.where("email.value").regex(term, "i"),
                        Criteria.where("phoneNumber.value").regex(term, "i")
                );
                andCriteria.add(searchCriteria);
            }

            // Filter by CustomerStatus
            if (filter.status() != null) {
                andCriteria.add(Criteria.where("status").is(filter.status().name()));
            }

            // Filter by CustomerSegment
            if (filter.segment() != null) {
                String segName = filter.segment().name();
                Criteria segCriteria = new Criteria().orOperator(
                        Criteria.where("segment.$id").is(segName),
                        Criteria.where("segment").is(segName)
                );
                andCriteria.add(segCriteria);
            }

            // Filter by Registration Date (createdAt)
            if (filter.fromDate() != null && filter.toDate() != null) {
                andCriteria.add(Criteria.where("createdAt").gte(filter.fromDate()).lte(filter.toDate()));
            } else if (filter.fromDate() != null) {
                andCriteria.add(Criteria.where("createdAt").gte(filter.fromDate()));
            } else if (filter.toDate() != null) {
                andCriteria.add(Criteria.where("createdAt").lte(filter.toDate()));
            }
        }

        Query query = new Query();
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        if (includePaginationAndSort && filter != null) {
            // Dynamic Sorting
            Sort.Direction direction = "asc".equalsIgnoreCase(filter.sortDirection())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            String sortBy = filter.sortBy() != null ? filter.sortBy() : "createdAt";

            Sort sort;
            if ("name".equalsIgnoreCase(sortBy)) {
                sort = Sort.by(direction, "familyName", "givenName");
            } else if ("registrationDate".equalsIgnoreCase(sortBy) || "createdAt".equalsIgnoreCase(sortBy)) {
                sort = Sort.by(direction, "createdAt");
            } else if ("status".equalsIgnoreCase(sortBy)) {
                sort = Sort.by(direction, "status");
            } else if ("username".equalsIgnoreCase(sortBy)) {
                sort = Sort.by(direction, "username");
            } else {
                sort = Sort.by(direction, "createdAt");
            }
            query.with(sort);

            // Pagination
            int page = Math.max(filter.page(), 0);
            int size = filter.size() > 0 ? filter.size() : 10;
            query.skip((long) page * size).limit(size);
        }

        return query;
    }

    private CustomerStatus parseCustomerStatus(Object obj) {
        if (obj == null) return CustomerStatus.ACTIVE;
        try {
            return CustomerStatus.valueOf(obj.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CustomerStatus.ACTIVE;
        }
    }

    private CustomerSegment parseCustomerSegment(Object obj) {
        if (obj == null) return CustomerSegment.SILVER;
        if (obj instanceof Document d) {
            Object idVal = d.get("$id");
            if (idVal != null) {
                try {
                    return CustomerSegment.valueOf(idVal.toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }
        } else if (obj instanceof DBRef dbRef) {
            try {
                return CustomerSegment.valueOf(dbRef.getId().toString().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        } else {
            try {
                return CustomerSegment.valueOf(obj.toString().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return CustomerSegment.SILVER;
    }

    private String parseStatusSegmentKey(Document doc) {
        Object idObj = doc.get("_id");
        if (idObj instanceof Document idDoc) {
            Object st = idDoc.get("status");
            Object sg = idDoc.get("segment");
            String stStr = st != null ? st.toString() : "UNKNOWN";
            String sgStr = "SILVER";
            if (sg instanceof Document d && d.get("$id") != null) {
                sgStr = d.get("$id").toString();
            } else if (sg instanceof DBRef ref) {
                sgStr = ref.getId().toString();
            } else if (sg != null) {
                sgStr = sg.toString();
            }
            return stStr + "_" + sgStr;
        }
        return "UNKNOWN";
    }

    private long getCount(Object countObj) {
        if (countObj instanceof Number n) {
            return n.longValue();
        }
        return 0L;
    }
}
