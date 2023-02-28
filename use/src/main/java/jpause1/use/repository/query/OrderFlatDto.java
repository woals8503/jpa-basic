package jpause1.use.repository.query;

import jpause1.use._enum.OrderStatus;
import jpause1.use.domain.Address;
import lombok.Data;

import java.time.LocalDateTime;

//order와 orderItem을 조인해서 가져오는 방식
@Data
public class OrderFlatDto {


    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

}
