package com.example.speedsideproject.quarydsl.post;


import com.example.speedsideproject.account.entity.Account;
import com.example.speedsideproject.post.Post;
import com.example.speedsideproject.post.enums.Category;
import com.example.speedsideproject.post.enums.Place;
import com.example.speedsideproject.post.enums.Tech;
import com.example.speedsideproject.security.user.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    //mypage에서 내가 like 한 postList  불러오기
    List<Post> findTop5ByMyLikes(Account account);

    //post get all
    Page<?> findAllPost(Pageable pageable);

    //    post - category 정렬
    Page<?> findAllPostWithCategory(Pageable pageable, List<Tech> techList);

    Page<?> findAllPostWithCategory3(Pageable pageable, List<Tech> techList, Category category, Place place);

    List<?> findAllPostWithCategory4(Long offset, Long size, List<Tech> techList, Category category, Place place);

    Page<?> findAllPostWithCategory5(Pageable pageable, List<Tech> techList, Category category, Place place);

    List<?> findAllPostWithCategory6(String sort, Long size, Long page, List<Tech> techList, Category category, Place place);

    Page<?> findAllPostWithCategory7(Pageable pageable, String sort, List<Tech> techList, Category category, Place place);

    Object findAllPostWithCategory8(Pageable pageable, String sort, List<Tech> techList, Category category, Place place, UserDetailsImpl userDetails);

}
