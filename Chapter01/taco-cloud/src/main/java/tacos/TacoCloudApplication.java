package tacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TacoCloudApplication {
    /* @SpringBootApplication
    => @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan

    @SpringBootConfiguration
    : 현재 클래스를 구성 클래스로 지정.

    @EnableAutoConfiguration
    : 스프링 부트 자동-구성 활성화

    @ComponentScan
    : 컴포넌트 검색 활성화 (@Component, @Controller, @Service)
     */

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}
    /* main() => 실제로 애플리케이션을 시작시키고 스프링 애플리케이션 컨텍스트를 생성하는 SpringApplication 의 run() 메서드 호출.
    run() 에 전달되는 두 개의 매개변수는 구성 클래스와 명령행(command-line) 인자다.
     */
}
