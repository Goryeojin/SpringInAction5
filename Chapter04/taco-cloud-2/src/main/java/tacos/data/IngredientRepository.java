package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.Ingredient;

/**
 * CrudRepository 인터페이스에는 CRUD 연산 메서드가 선언되어 있음.
 * 첫 번째 매개변수은 레포에 저장되는 개체 타입, 두 번째 매개변수는 개체 ID 속성 타입
 */
public interface IngredientRepository extends CrudRepository<Ingredient, String> {

//	Iterable<Ingredient> findAll();
//	Ingredient findById(String id);
//	Ingredient save(Ingredient ingredient);
}
