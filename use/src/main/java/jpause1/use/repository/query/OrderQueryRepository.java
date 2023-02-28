package jpause1.use.repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//엔티티가 아닌 것들을 조회할 때는 여기서 조회
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();  //1번
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());    // N번
            o.setOrderItems(orderItems);
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();  //이전의 단점은 루프를 돈다는 점
        
        //조회한 주문 건에서 주문 아이디를 한꺼번에 조회
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        
        //위에서 조회한 아이디를 가지고 주문 상품을 모두 조회
        List<OrderItemQueryDto> orderItems = em.createQuery("select " +
                        "new jpause1.use.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id in :orderIds", OrderItemQueryDto.class)  //주문 상품 중 주문 아이디가 포함된 것을 한꺼번에 조회
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                        .collect(Collectors.groupingBy(OrderItemQueryDto -> OrderItemQueryDto.getOrderId()));
        //각 주문 아이디 별로 상품을 그룹화 작업
        //ex 4 = [ OrderItemQueryDto(orderId=4, itemName=JPA1 BOOK, orderPrice=10000, count=1), OrderItemQueryDto(orderId=4, itemName=JPA2 BOOK, orderPrice=20000, count=2) ]

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        //초반 주문한 주문건에 대해서 map에서 주문 아이디를 가져와 그정보를 담는다.

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select " +
                        "new jpause1.use.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select " +
                                        "new jpause1.use.repository.query.OrderQueryDto(o.id, m.username, o.orderDate, o.status, d.address) " +
                                        "from Order o " +
                                        "join o.member m " +
                                        "join o.delivery d", OrderQueryDto.class)
                                        .getResultList();
    }


    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select " +
                "new jpause1.use.repository.query.OrderFlatDto(o.id, m.username, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d " +
                "join o.orderItems oi " +
                "join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
