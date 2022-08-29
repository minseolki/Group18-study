package com.springboot.w3.springboot_w3.Dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
public class RecommentLike {

    @Id // ID 값, Primary Key로 사용하겠다는 뜻입니다.
    @GeneratedValue(strategy = GenerationType.AUTO) // 자동 증가 명령입니다.
    private Long id;

    @JoinColumn(name = "recomment_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Recomment recomment;

    @Column(nullable = false) // 컬럼 값이고 반드시 값이 존재해야 함을 나타냅니다.
    private String name;

    public RecommentLike(String username, Recomment recomment){
        this.name = username;
        this.recomment = recomment;
    }
}
