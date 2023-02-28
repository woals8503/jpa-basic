package jpause1.use.controller;

import jpause1.use.domain.Member;
import jpause1.use.domain.Order;
import jpause1.use.domain.item.Item;
import jpause1.use.repository.OrderRepository;
import jpause1.use.repository.OrderSearch;
import jpause1.use.service.ItemService;
import jpause1.use.service.MemberService;
import jpause1.use.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService oService;
    private final MemberService mService;
    private final ItemService iService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = mService.findMembers();
        List<Item> items = iService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        oService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = oService.findOrder(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        oService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
