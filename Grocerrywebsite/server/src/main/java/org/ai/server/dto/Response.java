package org.ai.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;
    private String message;
    private boolean success;
    private UserDto userDto;
    private AddressDto addressDto;
    private ProductDto productDto;
    private OrderDto orderDto;
    private OrderItemDto orderItemDto;
    private List<UserDto>userDtoList;
    private List<ProductDto> productDtoList;
    private List<OrderItemDto> orderItemDtoList;
    private List<OrderDto>orderDtoList;
    private List<AddressDto>addressDtoList;

    //optional
    private  String token;
    private String expirationTime;
    private String stripeUrl;


    // Success factory method
    public static Response success(String message) {
        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);
        response.setStatusCode(200);
        return response;
    }

    // Error factory method
    public static Response error(String message, int statusCode) {
        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(false);
        response.setStatusCode(statusCode);
        return response;
    }

    // User methods
    public Response withUser(UserDto user) {
        this.userDto = user;
        this.userDtoList = null;
        return this;
    }

    public Response withUsers(List<UserDto> users) {
        this.userDtoList = users;
        this.userDto = null;
        return this;
    }

    //Address methods
    public Response withAddress(AddressDto address) {
        this.addressDto = address;
        this.addressDtoList = null;
        return this;
    }

    public Response withAddress(List<AddressDto> address) {
        this.addressDtoList = address;
        this.addressDto = null;
        return this;
    }

    //Product methods
    public Response withProduct(ProductDto product) {
        this.productDto = product;
        this.productDtoList = null;
        return this;
    }

    public Response withProducts(List<ProductDto> products) {
        this.productDtoList = products;
        this.productDto = null;
        return this;
    }

    //Order item methods
    public Response withOrderItem(OrderItemDto orderItem) {
        this.orderItemDto = orderItem;
        this.orderItemDtoList = null;
        return this;
    }

    public Response withOrderItems(List<OrderItemDto> orderItemDtoList) {
        this.orderItemDtoList = orderItemDtoList;
        this.orderItemDto = null;
        return this;
    }


    //Order methods

    public Response withOrder(OrderDto order) {
        this.orderDto = order;
        this.orderDtoList = null;
        return this;
    }

    public Response withOrders(List<OrderDto> orderDtoList) {
        this.orderDtoList = orderDtoList;
        this.orderDto = null;
        return this;
    }
    public Response withTokenAndExpirationTime(String token, String expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
        return this;
    }
    public Response withStripeUrl(String stripeUrl) {
        this.stripeUrl = stripeUrl;
        return this;
    }





}
