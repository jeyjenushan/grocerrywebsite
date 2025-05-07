package org.ai.server.serviceimpl;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.ai.server.Repository.AddressRepository;
import org.ai.server.Repository.OrderRepository;
import org.ai.server.Repository.ProductRepository;
import org.ai.server.Repository.UserRepository;
import org.ai.server.dto.Response;
import org.ai.server.enumPackage.PaymentType;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.*;
import org.ai.server.request.OrderItemRequest;
import org.ai.server.request.PlaceOrderRequest;
import org.ai.server.request.ProductData;
import org.ai.server.service.OrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service

public class OrderServiceHandler implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceHandler(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;






    @Value("${stripe.api.key}")
    private String stripeApiKey;



    @Override
    public Response getUserOrders(Long userId) {
        try{
            UserEntity user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("The user is not Found"));
            List<OrderEntity> orders = orderRepository.findByUserIdOrIsPaid(
                userId,
                    true);
            if(orders.isEmpty()){
                return Response.success("No Orders Found").withOrders(DtoConverter.convertOrderListToOrderDto(orders));
            }
            else{
                return Response.success("Orders Found").withOrders(DtoConverter.convertOrderListToOrderDto(orders));

            }

        } catch (RuntimeException e) {
            return Response.error(e.getMessage(),400);

        }catch (Exception e) {
            return Response.error("There was an error getting the orders",400);
        }

    }

    @Override
    public Response getAllOrders() {
        try{
            List<OrderEntity> orders = orderRepository.findByIsPaid( true);
            if(orders.isEmpty()){
                return Response.success("No Orders Found");
            }
            return Response.success("Orders Found").withOrders(DtoConverter.convertOrderListToOrderDto(orders));
        } catch (Exception e) {
            return Response.error("There was an error getting the orders",400);
        }
    }

    @Override
    public Response placeOrderCOD(Long userId, PlaceOrderRequest placeOrderRequest) {
    try{
        if (placeOrderRequest.getAddress() == null || placeOrderRequest.getItems().isEmpty()) {
            return Response.error("Address or Order Items cannot correctly specified",404);

        }
        double amount = calculateOrderAmount(placeOrderRequest.getItems());
        amount += amount * 0.02; // Add 2% tax

        OrderEntity order=new OrderEntity();
        order.setUserId(userId);
        order.setPaymentType(PaymentType.COD);
        order.setAmount(amount);
        order.setItems(mapToOrderItems(placeOrderRequest.getItems()));
        order.setAddress(placeOrderRequest.getAddress());
        order.setPaid(false);
        OrderEntity savedOrder = orderRepository.save(order);
        return Response.success("Order can be created successfully")
                .withOrder(DtoConverter.convertOrdertoOrderDto(savedOrder));




    } catch (Exception e) {
        return Response.error("There was an error place order",400);

    }
    }

    @Override
    public OrderEntity getOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    private double calculateOrderAmount(List<OrderItemRequest> items) {
        return items.stream()
                .mapToDouble(item -> {
                    ProductEntity product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProduct().getId()));
                    return product.getOfferPrice() * item.getQuantity();
                })
                .sum();
    }

    private List<OrderItemEntity> mapToOrderItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(item -> {
                    OrderItemEntity orderItem = new OrderItemEntity();
                    orderItem.setProduct(productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found")));
                    orderItem.setQuantity(item.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public Response placeOrderStripe(Long userId, PlaceOrderRequest placeOrderRequest,HttpServletRequest request
                                   ){

        Stripe.apiKey = stripeApiKey;

        try{


            List<OrderItemRequest> items = placeOrderRequest.getItems();
            if (placeOrderRequest.getAddress() == null || placeOrderRequest.getItems().isEmpty()) {
                return Response.error("Address or Order Items cannot correctly specified",404);

            }

            // Save address explicitly
            AddressEntity address = placeOrderRequest.getAddress();
            address = addressRepository.save(address);

            // Calculate amount and prepare product data
            List<ProductData> productDataList = new ArrayList<>();
            double amount = 0.0;


            for (OrderItemRequest item : items) {
                ProductEntity product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                productDataList.add(new ProductData(
                        product.getName(),
                        product.getOfferPrice(),
                        item.getQuantity()
                ));

                amount += product.getOfferPrice() * item.getQuantity();
            }

            // Add tax (2%)
            amount += Math.floor(amount * 0.02);

            // Create order
            OrderEntity order = new OrderEntity();
            order.setUserId(userId);
            order.setItems(mapToOrderItems(placeOrderRequest.getItems()));

            order.setAmount(amount);
            order.setAddress(address);

            order.setPaymentType(PaymentType.STRIPE);
            order.setPaid(false);


            order = orderRepository.save(order);





            // Prepare line items for Stripe
            List<SessionCreateParams.LineItem> lineItems = productDataList.stream()
                    .map(item -> {
                        long unitAmount = (long) (Math.floor(item.getPrice() + item.getPrice() * 0.02) * 100);
                        return SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(item.getName())
                                                                .build()
                                                )
                                                .setUnitAmount(unitAmount)
                                                .build()
                                )
                                .setQuantity((long) item.getQuantity())
                                .build();
                    })
                    .collect(Collectors.toList());



            // Create Stripe session
            SessionCreateParams params = SessionCreateParams.builder()
                    .addAllLineItem(lineItems)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:5173" + "/loader?success=true&orderId=" + order.getId())
                    .setCancelUrl("http://localhost:5173" + "/loader?success=false&orderId=" + order.getId())
                    .build();


            System.out.println("jenushan");

            Session session = Session.create(params);


            return Response.success("The Stripe Url is fetched successfully").withStripeUrl(session.getUrl());



        } catch (Exception e) {
          return Response.error("There was an error place order",400);
        }
    }


    @Override
    public Response verifyStripePayment(Map<String, String> payload) {
        Response response = new Response();
        try {

            String orderId = payload.get("orderId");
            String success = payload.get("success");

            if (success.equals("true")) {
                OrderEntity orderEntity = getOrderId(Long.valueOf(orderId));

                if (orderEntity != null) {

                    // Save the updated entity
                    orderEntity.setPaid(true);
                    OrderEntity savedOrder=orderRepository.save(orderEntity);
                    System.out.println("Order"+savedOrder);
                    return Response.success("Order can be updated and Payment successfully").withOrder(DtoConverter.convertOrdertoOrderDto(savedOrder));




                } else {
                    return Response.error("The order is not found",400);
                }
            } else {

                return Response.error("Payment Failed",400);
            }

        } catch (Exception e) {

            return  Response.error(e.getMessage(),400);
        }

    }



}
