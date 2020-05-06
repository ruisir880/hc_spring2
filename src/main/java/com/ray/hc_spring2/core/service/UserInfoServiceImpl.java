package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.PageQueryRepository;
import com.ray.hc_spring2.core.repository.UserInfoRepository;
import com.ray.hc_spring2.model.UserInfo;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService{
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PageQueryRepository<UserInfo, Long> pageQueryRepository;


    @Transactional(readOnly=true)
    @Override
    public UserInfo findByUsername(String username) {
        LOGGER.info("UserInfoServiceImpl.findById() :" + username );
        return userInfoRepository.findByUsername(username);
    }

    @Override
    public UserInfo saveUser(UserInfo userInfo) {
        LOGGER.info("UserInfoServiceImpl.saveUser() :" + userInfo.getRealName() );
        return userInfoRepository.save(userInfo);
    }

    @Override
    public Page<UserInfo> pageQuery(int page) {
        return pageQueryRepository.findAll(new PageRequest(page,Constants.PAGE_SIZE));
    }

    public Page<UserInfo> pageUserQuery(int page ,String username,String realName,String mobile){
        Specification<UserInfo> specification = new Specification<UserInfo>() {
            @Override
            public Predicate toPredicate(Root<UserInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>(); //所有的断言
                if(StringUtils.hasText(username)){
                    predicates.add(cb.like(root.get("username"), "%"+username+"%"));
                }
                if(StringUtils.hasText(realName)){
                    predicates.add(cb.like(root.get("realName"),"%"+realName+"%"));
                }
                if(StringUtils.hasText(mobile)){
                    predicates.add(cb.like(root.get("mobile"), "%"+mobile+"%"));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        //分页信息
        Pageable pageable = new PageRequest(page-1, Constants.PAGE_SIZE); //页码：前端从1开始，jpa从0开始，做个转换
        //查询
        return this.userInfoRepository.findAll(specification,pageable);
    }


    public List<UserInfo> findByCondition(String username,String realName,String mobile){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserInfo> query  = criteriaBuilder.createQuery(UserInfo.class);
        Root<UserInfo> root =  query.from(UserInfo.class);
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasText(username)){
            predicates.add(criteriaBuilder.like(root.get("username"), "%"+username+"%"));
        }
        if(StringUtils.hasText(realName)){
            predicates.add(criteriaBuilder.like(root.get("realName"),"%"+realName+"%"));
        }
        if(StringUtils.hasText(mobile)){
            predicates.add(criteriaBuilder.like(root.get("mobile"), "%"+mobile+"%"));
        }
        Predicate[] predicateArr = new Predicate[predicates.size()];
        predicateArr = predicates.toArray(predicateArr);
        query.where(predicateArr);
        return entityManager.createQuery(query).getResultList();
    }


    @Override
    public UserInfo findById(long userId) {
        return userInfoRepository.findOne(userId);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        userInfoRepository.delete(userId);
    }
}
