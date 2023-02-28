package jpause1.use.service;

import jpause1.use.domain.item.Book;
import jpause1.use.domain.item.Item;
import jpause1.use.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = false)
    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    
    //변경 감지 기능 사용
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // 수정할 때 이렇게 파라미터를 가져와 findOne으로 영속성 컨텍스트에 넣으면
        // 영속성 컨텍스트가 관리하기 때문에 트랜잭션을 통해 flush가 될 때.
        // 더티체킹이 일어나 바뀐 부분을 수정한다.
        Item findItem = itemRepository.findOne(itemId);
        findItem.change(name, price, stockQuantity, findItem);
        // 만약 업데이트할게 많으면?
        // DTO를 따로 만들어서 거기서 get해서 불러오는 방식
    }


    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
