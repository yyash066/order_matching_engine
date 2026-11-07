package com.harsh.orderservice.controllers;

import com.harsh.matchingEngine.engine.MatchingEngineService;
import com.harsh.matchingEngine.models.Order;
import com.harsh.matchingEngine.models.Trade;
import com.harsh.orderservice.dto.SubmitOrderReqDto;
import com.harsh.orderservice.dto.SubmitOrderResDto;
import com.harsh.orderservice.mappers.OrderMapper;
import com.harsh.orderservice.websocket.OrderBookBroadcaster;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "APIs for submitting, retrieving, and canceling orders")
public class OrderController {
    private final MatchingEngineService matchingEngineService;
    private final OrderMapper orderMapper;
    private final OrderBookBroadcaster orderBookBroadcaster;

    public OrderController(MatchingEngineService matchingEngineService,
                           OrderMapper orderMapper,
                           OrderBookBroadcaster orderBookBroadcaster) {
        this.matchingEngineService = matchingEngineService;
        this.orderMapper = orderMapper;
        this.orderBookBroadcaster = orderBookBroadcaster;
    }

    @PostMapping
    public ResponseEntity<SubmitOrderResDto> submitOrder(
            @RequestBody @Valid SubmitOrderReqDto request) throws Exception {

        Order order = orderMapper.mapToOrder(request);
        List<Trade> trades = matchingEngineService.submitOrder(order);

        // Broadcast updated order book
        orderBookBroadcaster.broadcastOrderBook(order.getSymbol());

        return ResponseEntity.created(new URI("/orders/" + order.getOrderId()))
                .body(orderMapper.mapToOrderResDto(order, trades));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = matchingEngineService.getOrder(orderId);

        if (order == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(order);
    }

    @GetMapping("/orderbook/{symbol}")
    public ResponseEntity<Void> getOrdersForSymbolWS(@PathVariable String symbol) throws Exception {
        orderBookBroadcaster.broadcastOrderBook(symbol);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) throws Exception {
        Order order = matchingEngineService.getOrder(orderId);
        boolean result = matchingEngineService.cancelOrder(orderId);

        // Broadcast updated order book if cancellation was successful
        if (result && order != null)
            orderBookBroadcaster.broadcastOrderBook(order.getSymbol());

        return result ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
