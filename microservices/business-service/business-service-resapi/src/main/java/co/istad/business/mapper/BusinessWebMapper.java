package co.istad.business.mapper;

import co.istad.business.dto.ProductRequest;
import co.istad.business.dto.ProductResponse;
import co.istad.business.dto.RegisterBusinessRequest;
import co.istad.business.dto.RegisterBusinessResponse;
import co.istad.business.dto.register.ProductCommand;
import co.istad.business.dto.register.ProductResult;
import co.istad.business.dto.register.RegisterBusinessCommand;
import co.istad.business.dto.register.RegisterBusinessResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusinessWebMapper {

    RegisterBusinessCommand toCommand(RegisterBusinessRequest request);

    ProductCommand toCommand(ProductRequest request);

    RegisterBusinessResponse toResponse(RegisterBusinessResult result);

    ProductResponse toResponse(ProductResult result);
}
