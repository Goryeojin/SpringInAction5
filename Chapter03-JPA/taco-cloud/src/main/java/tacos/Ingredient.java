package tacos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data // 인자가 있는 생성자를 자동으로 추가한다.
@RequiredArgsConstructor // 하지만 NoArgsConstructor 가 지정되면 생성자가 제거되기 때문에 RequiredArgsConstructor 지정.
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true) // JPA 개체는 인자가 없는 생성자를 가져야 함.
// 클래스 외부에서 사용하지 못하도록 protected / 또는 public 으로 생성해야 Entity 로 사용 가능
// 초기화가 필요한 final 속성이 있으니 force = true
@Entity // JPA 개체로 선언하기 위한 어노테이션
public class Ingredient {

    @Id // 해당 속성이 데이트베이스의 개체를 고유하게 식별함. 아이디를 직접 지정해준다.
	private final String id;
	private final String name;
	private final Type type;

	public static enum Type {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
