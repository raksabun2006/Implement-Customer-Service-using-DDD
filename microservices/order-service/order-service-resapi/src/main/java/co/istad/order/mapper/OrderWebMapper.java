package co.istad.order.mapper;

import co.istad.order.dto.*;
import co.istad.order.dto.create.CreateOrderCommand;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.dto.create.OrderAddressCommand;
import co.istad.order.dto.create.OrderItemCommand;
import co.istad.order.dto.track.TrackOrderResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderWebMapper {

    CreateOrderCommand toCommand(CreateOrderRequest request);

    OrderAddressCommand toCommand(OrderAddressRequest request);

    OrderItemCommand toCommand(OrderItemRequest request);

    CreateOrderResponse toResponse(CreateOrderResult result);

    TrackOrderResponse toResponse(TrackOrderResult result);
}
