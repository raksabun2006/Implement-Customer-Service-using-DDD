package co.istad.bean;

import co.istad.service.CustomerDomainService;
import co.istad.service.CustomerDomainServiceImpl;
import co.istad.service.CustomerSegmentationService;
import co.istad.service.CustomerSegmentationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainServiceImpl();
    }

    @Bean
    public CustomerSegmentationService customerSegmentationService() {
        return new CustomerSegmentationServiceImpl();
    }

}
