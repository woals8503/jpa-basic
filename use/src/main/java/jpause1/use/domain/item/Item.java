package jpause1.use.domain.item;

import jpause1.use.domain.Category;
import jpause1.use.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@DiscriminatorColumn(name = "dtype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int qunatity) {
        int restStock = this.stockQuantity - qunatity;  // 현재 재고 - 주문 량
        if(restStock < 0) {
            throw new NotEnoughStockException("재고가 없습니다");
        }
        this.stockQuantity = restStock;
    }

    public void change(String name, int price, int stockQuantity, Item findItem) {
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }
}
