package com.example.speedsideproject.post;


import com.example.speedsideproject.account.entity.Account;
import com.example.speedsideproject.applyment.entity.Applyment;
import com.example.speedsideproject.global.Timestamped;
import com.example.speedsideproject.likes.Likes;
import com.example.speedsideproject.post.enums.Category;
import com.example.speedsideproject.post.enums.Place;
import com.example.speedsideproject.post.enums.PostState;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Entity
public class Post extends Timestamped {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true)
    private String contents;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = true)
    private Long duration;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Place place;

    //모집인원
    private Long frontReqNum = 0L;
    private Long backReqNum = 0L;
    private Long designReqNum = 0L;
    private Long pmReqNum = 0L;
    private Long mobileReqNum = 0L;

    //지원 인원
    @Setter
    private Long backendNum = 0L;
    @Setter
    private Long frontendNum = 0L;
    @Setter
    private Long designNum = 0L;
    @Setter
    private Long mobileNum = 0L;
    @Setter
    private Long pmNum = 0L;
    @Column(nullable = true)
    private String startDate;

    //html소스로 된 contents
    @Column(nullable = true)
    private String contentUrl;
    @Column(nullable = true)
    private String contentKey;
    //조회수
    @Setter
    @Column(nullable = true)
    private Long viewCount = 0L;

    //연관관계 시작
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_Id")
    private Account account;

    //One post to Many applyment
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Applyment> applyment = new ArrayList<>();

    // One Post To Many Likes
    //cascade 어떻게 할것인지.....
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Likes> likes;

    @Column(nullable = true)
    private Long likesLength = 0L;

    //one post to many tech
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Techs> techs = new ArrayList<>();

    //모집중 or 모집완료
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private PostState postState;
    //one post to many images
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    private List<Image> imageList = new ArrayList<>();

    //    public Post(PostRequestDto requestDto, Account account) {
//        this.contents = requestDto.getContents();
//        this.title = requestDto.getTitle();
//        this.account = account;
//        this.category = requestDto.getCategory();
//        this.duration = requestDto.getDuration();
//        this.peopleNum = requestDto.getPeopleNum();
//        this.place = requestDto.getPlace();
//        this.startDate = requestDto.getStartDate();
//        this.postState = requestDto.getPostState();
//    }

    //create v2
    public Post(PostRequestDto2 requestDto, Account account, Map<String, String> urlMap) {
        this.title = requestDto.getTitle();
        this.account = account;
        this.category = requestDto.getCategory();
        this.duration = requestDto.getDuration();
        this.place = requestDto.getPlace();
        this.startDate = requestDto.getStartDate();
        this.postState = requestDto.getPostState();
        this.frontReqNum = requestDto.getFrontReqNum();
        this.backReqNum = requestDto.getBackReqNum();
        this.designReqNum = requestDto.getDesignReqNum();
        this.pmReqNum = requestDto.getPmReqNum();
        this.mobileReqNum = requestDto.getMobileReqNum();
        this.contentUrl = urlMap.get("url");
        this.contentKey = urlMap.get("key");
    }

    //method
    //글내용만 업데이트 v1
//    public void update(PostRequestDto requestDto) {
//        this.title = requestDto.getTitle();
//        this.contents = requestDto.getContents();
//        this.category = requestDto.getCategory();
//        this.duration = requestDto.getDuration();
//        this.peopleNum = requestDto.getPeopleNum();
//        this.place = requestDto.getPlace();
//        this.startDate = requestDto.getStartDate();
//        this.postState = requestDto.getPostState();
//    }

    //글내용 업데이트 v2
    public void update2(PostRequestDto2 requestDto, Map<String, String> urlMap) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.duration = requestDto.getDuration();
        this.frontReqNum = requestDto.getFrontReqNum();
        this.backReqNum = requestDto.getBackReqNum();
        this.designReqNum = requestDto.getDesignReqNum();
        this.pmReqNum = requestDto.getPmReqNum();
        this.mobileReqNum = requestDto.getMobileReqNum();
        this.place = requestDto.getPlace();
        this.startDate = requestDto.getStartDate();
        this.postState = requestDto.getPostState();
        this.contentUrl = urlMap.get("url");
        this.contentKey = urlMap.get("key");
    }

    //라이크의 갯수를 추가하는 메소드
    public void setLikesLength(boolean likesType) {
        this.likesLength = (likesType) ? this.likesLength + 1L : this.likesLength - 1L;
    }

    // html url Map
    public void setContent(Map<String, String> content) {
        this.contentUrl = content.get("url");
        this.contentKey = content.get("key");
    }

    public void addTechs(Techs techs) {
        this.techs.add(techs);
    }


    //연관관계 맵핑
/*    public void addImg(Image image) {
        this.imageList.add(image);
        image.setPost(this);
    }*/

}

