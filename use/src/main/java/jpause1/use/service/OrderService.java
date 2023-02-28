package jpause1.use.service;

import jpause1.use.domain.Delivery;
import jpause1.use.domain.Member;
import jpause1.use.domain.Order;
import jpause1.use.domain.OrderItem;
import jpause1.use.domain.item.Item;
import jpause1.use.repository.ItemRepository;
import jpause1.use.repository.MemberRepository;
import jpause1.use.repository.OrderRepository;
import jpause1.use.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional(readOnly = false)
    public Long order(Long memberId, Long itemId, int count) {

        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);    // cascade옵션 때문에 하나만 persist해도 전부 들어간다

        return order.getId();
    }

    //주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

//    검색
    public List<Order> findOrder(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }

}
