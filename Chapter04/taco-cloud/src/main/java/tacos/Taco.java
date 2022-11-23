package tacos;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Entity // JPA 개체 선언
public class Taco {

    @Id // 해당 특성이 데이터베이스의 개체를 고유하게 식별함. 데이터베이스가 자동으로 생성해 주는 ID 값 사용을 위해서
    @GeneratedValue(strategy = GenerationType.AUTO) // GeneratedValue 어노테이션 사용
	private Long id;
	private Date createdAt;

	@NotNull
	@Size(min = 5, message = "Name must be at least 5 characters long")
	private String name;

    @ManyToMany(targetEntity = Ingredient.class)
    // 하나의 Taco 객체는 많은 Ingredient 를 가질 수 있고, 하나의 Ingredient 는 여러 Taco 객체에 포함될 수 있다.
	@NotNull
	@Size(min = 1, message = "You must choose at least 1 ingredient")
	private List<Ingredient> ingredients;

    @PrePersist // Taco 객체가 저장되기 전 createdAt 속성을 현재 일자와 시간으로 설정
    void createdAt() {
        this.createdAt = new Date();
    }
}
