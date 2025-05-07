package org.ai.server.mapper;

import org.ai.server.dto.*;
import org.ai.server.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

    public static UserDto convertUsertoUserDto(UserEntity user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setImage(user.getImage());
        userDto.setRole(user.getRole());
        System.out.println(user.getCartItems());
        userDto.setCartItems(user.getCartItems());
        return userDto;

    }

    public static List<UserDto> convertUserListToUserDto(List<UserEntity> userList){
        return userList.stream().map(DtoConverter::convertUsertoUserDto).collect(Collectors.toList());
    }
    public static AddressDto convertAddresstoAddressDto(AddressEntity address){
        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setCity(address.getCity());
        addressDto.setCountry(address.getCountry());
        addressDto.setStreet(address.getStreet());
        addressDto.setFirstName(address.getFirstName());
        addressDto.setLastName(address.getLastName());
        addressDto.setEmail(address.getEmail());
        addressDto.setPhone(address.getPhone());
        addressDto.setUserId(address.getUserId());
        addressDto.setState(address.getState());
        addressDto.setZipcode(address.getZipcode());
        return addressDto;


    }
    public static List<AddressDto> convertAddressListToAddressDtoList(List<AddressEntity> addressList){
        return addressList.stream().map(DtoConverter::convertAddresstoAddressDto).collect(Collectors.toList());
    }

    public static ProductDto convertProducttoProductDto(ProductEntity product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setCategory(product.getCategory());
        productDto.setImage(product.getImage());
        productDto.setInStock(product.getInStock());
        productDto.setOfferPrice(product.getOfferPrice());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());

        return productDto;

    }
    public static List<ProductDto> convertProductListToProductDto(List<ProductEntity> productList){
        return productList.stream().map(DtoConverter::convertProducttoProductDto).collect(Collectors.toList());
    }


    public static OrderItemDto convertOrderItemtoOrderItemDto(OrderItemEntity orderItem){
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setQuantity(orderItem.getQuantity());
        if(orderItem.getProduct() != null){
            orderItemDto.setProduct(convertProducttoProductDto(orderItem.getProduct()));
        }
        return orderItemDto;

    }
    public static List<OrderItemDto> convertOrderItemListToOrderItemDto(List<OrderItemEntity> orderItemList){
        return orderItemList.stream().map(DtoConverter::convertOrderItemtoOrderItemDto).collect(Collectors.toList());
    }

    public static OrderDto convertOrdertoOrderDto(OrderEntity order){
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setAddress(convertAddresstoAddressDto(order.getAddress()));
        orderDto.setAmount(order.getAmount());
        orderDto.setUserId(order.getUserId());
        orderDto.setStatus(order.getStatus());
        orderDto.setPaymentType(order.getPaymentType());
        orderDto.setPaid(order.isPaid());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setUpdatedAt(order.getUpdatedAt());
        if(order.getItems() != null){
            orderDto.setOrderItems(convertOrderItemListToOrderItemDto(order.getItems()));
        }



        return orderDto;

    }

    public static List<OrderDto> convertOrderListToOrderDto(List<OrderEntity> orderList){
        return orderList.stream().map(DtoConverter::convertOrdertoOrderDto).collect(Collectors.toList());
    }

}
