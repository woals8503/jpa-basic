package jpause1.use.repository;

import jpause1.use.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) em.persist(item);  // 신규 등록
        else em.merge(item);                        // DB에서 가져오는것
        // merge는 updateItem()의 코드를 대신해서 사용 가능하다.
        // 쉽게말해 바꿔치기한다. 하지만 영속성 컨텍스트로 관리는 안된다.
        // 하지만 병합 시 값이 없다면 null로 업데이트될 수도 있어 위험하다.
        // 그래서 기존에 있던 데이터가 null이 된다.
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

}
