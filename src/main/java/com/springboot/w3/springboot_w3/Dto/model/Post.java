package com.springboot.w3.springboot_w3.Dto.model;

import com.springboot.w3.springboot_w3.Dto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;


@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
public class Post extends Timestamped {

    @Id // ID 값, Primary Key로 사용하겠다는 뜻입니다.
    @GeneratedValue(strategy = GenerationType.AUTO) // 자동 증가 명령입니다.
    private Long id;

    @Column(nullable = false) // 컬럼 값이고 반드시 값이 존재해야 함을 나타냅니다.
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String imgUrl;


    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(nullable = false)
    private int likeNum;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    @Column(nullable = false)
    private int commentNum;

    public void update(PostRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void updateCommentNum(int num){
        this.commentNum = num;
        System.out.println("업데이트 : " + this.commentNum);
    }
    public void updateLikeNum(int num){
        this.likeNum = num;
        System.out.println("업데이트 : " + this.likeNum);
    }

    public Post(PostRequestDto postRequestDto, String username){
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.name = username;
        this.imgUrl = postRequestDto.getImgUrl();
        this.likeNum=0;
        this.commentNum = 0;
    }

}
